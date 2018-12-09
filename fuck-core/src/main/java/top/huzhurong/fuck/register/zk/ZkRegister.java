package top.huzhurong.fuck.register.zk;

import com.github.zkclient.ZkClient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.NumberUtils;
import top.huzhurong.fuck.proxy.ProviderSet;
import top.huzhurong.fuck.register.IRegister;
import top.huzhurong.fuck.transaction.support.Consumer;
import top.huzhurong.fuck.transaction.support.Provider;
import top.huzhurong.fuck.util.NetUtils;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 提供者启动的时候发布服务到zookeeper
 * 消费者启动的时候从zookeeper拉取服务提供者，并且订阅服务
 *
 * @author luobo.cs@raycloud.com
 * @since 2018/12/1
 */
@Slf4j
public class ZkRegister implements IRegister {

    final static String root_path = "/fuck";
    final static String root_provider = "/provider";
    private final static String root_consumer = "/consumer";
    private final static String line = "/";
    private final static String split = "#A#";

    private final static Integer default_session_timeout = 1000;
    private final static Integer default_connection_timeout = 10000;

    @Getter
    @Setter
    private ZkClient zkClient;

    @Getter
    @Setter
    private Integer session = default_session_timeout;
    @Getter
    @Setter
    private Integer connection = default_connection_timeout;

    public ZkRegister(String host) {
        this(host, default_session_timeout, default_connection_timeout);
    }

    public ZkRegister(String host, Integer sessionOut, Integer connectionOut) {
        assert host != null;
        assert sessionOut > 0;
        assert connectionOut > 0;
        zkClient = new ZkClient(host, sessionOut, connectionOut);
        if (!zkClient.exists(root_path)) {
            zkClient.createPersistent(root_path, "fuck-rpc root path".getBytes(Charset.forName("utf-8")));
        }
    }

    @Override
    public void registerService(List<Provider> providerList) {
        assert zkClient != null;
        providerList.parallelStream().forEach(provider -> {
            String host = provider.getHost();
            Integer port = provider.getPort();
            String serviceName = provider.getServiceName();
            String version = provider.getVersion();
            Integer weight = provider.getWeight();
            String serverPath = root_path + "/" + serviceName + root_provider;
            if (!zkClient.exists(serverPath)) {
                zkClient.createPersistent(serverPath, true);
            }
            String finalInfo = host + split + port + split + serviceName + split + version + split + weight + split + provider.getSerialization();
            String path = serverPath + "/" + finalInfo;
            if (!zkClient.exists(path)) {
                log.info("注册服务:{}到ZooKeeper", serverPath);
                zkClient.createEphemeral(path);
            } else {
                log.warn("服务:{}已被注册", serverPath);
            }
        });
    }

    @Override
    public List<Provider> discover(String serviceName, String version) {
        assert zkClient != null;
        String serverPath = root_path + "/" + serviceName + root_provider;
        List<String> children = null;
        try {
            children = zkClient.getChildren(serverPath);
        } catch (Exception ex) {
            if (ex.getMessage().contains("KeeperException$NoNodeException") && ex.getMessage().contains("KeeperErrorCode")) {
                throw new RuntimeException("提供者[" + serviceName + "]服务列表为空");
            } else {
                ex.printStackTrace();
                System.exit(1);
            }
        }
        if (children == null || children.size() == 0) {
            throw new RuntimeException("提供者列表为空");
        }
        List<Provider> collect = children.parallelStream().map(this::toProvider).collect(Collectors.toList());
        List<Provider> result = collect.stream().filter(provider -> provider.getVersion().equalsIgnoreCase(version)).collect(Collectors.toList());
        if (result == null || result.size() == 0) {
            throw new RuntimeException("版本号:[" + version + "]的提供者列表为空");
        }
        return result;
    }

    @Override
    public void registerConsumer(Consumer consumer) {
        Assert.notNull(consumer, "消费者信息不能为空");
        String host = consumer.getHost();
        String version = consumer.getVersion();
        Integer timeout = consumer.getTimeout();
        String consumerPath = root_path + "/" + consumer.getServiceName() + root_consumer;
        if (!zkClient.exists(consumerPath)) {
            zkClient.createPersistent(consumerPath, true);
        }
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        Integer integer = Integer.valueOf(runtimeMXBean.getName().split("@")[0]);
        String finalInfo = host + split + consumer.getServiceName() + split + version + split + timeout + split + integer;
        String path = consumerPath + "/" + finalInfo;
        if (!zkClient.exists(path)) {
            zkClient.createEphemeral(path);
        }
    }

    @Override
    public void subscribe(String service) {
        Assert.notNull(this.zkClient, "zkClient不能为空");
        String finalPath = root_path + line + service  + root_provider;
        this.zkClient.subscribeChildChanges(finalPath, (parent, children) -> {
            if (log.isDebugEnabled()) {
                log.debug("notify {} about subscribe server:{},provider list:{}", NetUtils.getLocalHost(), service, children);
            }
            List<Provider> all = ProviderSet.getAll(service);
            List<Provider> collect = children.stream().map(this::toProvider).collect(Collectors.toList());
            if (all.equals(collect)) {
                log.debug("提供者没有变更");
            } else {
                ProviderSet.reset(service, collect);
            }
        });
    }

    private Provider toProvider(String child) {
        assert child != null;
        String[] info = child.split(ZkRegister.split);
        assert info.length == 6;
        Provider provider = new Provider();
        provider.setHost(info[0]);
        provider.setPort(Integer.valueOf(info[1]));
        provider.setServiceName(info[2]);
        provider.setVersion(info[3]);
        provider.setWeight(NumberUtils.parseNumber(info[4], Integer.class));
        provider.setSerialization(info[5]);
        return provider;
    }
}
