package top.huzhurong.fuck.register.zk;

import com.github.zkclient.ZkClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import top.huzhurong.fuck.register.IRegister;
import top.huzhurong.fuck.transaction.support.Provider;

import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/1
 */
@Slf4j
public class ZkRegister implements IRegister {

    private final static String root_path = "/fuck";
    private final static String split = "#A#";

    private final static Integer default_session_timeout = 1000;
    private final static Integer default_connection_timeout = 10000;

    private ZkClient zkClient;

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
            String serverPath = root_path + "/" + serviceName + "/provider";
            if (!zkClient.exists(serverPath)) {
                zkClient.createPersistent(serverPath, true);
            }
            String finalInfo = host + split + port + split + serviceName + split + version;
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
        String serverPath = root_path + "/" + serviceName + "/provider";
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

    private Provider toProvider(String child) {
        assert child != null;
        String[] info = child.split(ZkRegister.split);
        assert info.length == 4;
        Provider provider = new Provider();
        provider.setHost(info[0]);
        provider.setPort(Integer.valueOf(info[1]));
        provider.setServiceName(info[2]);
        provider.setVersion(info[3]);
        return provider;
    }
}
