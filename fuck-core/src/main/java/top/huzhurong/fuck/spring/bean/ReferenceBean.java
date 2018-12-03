package top.huzhurong.fuck.spring.bean;

import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import top.huzhurong.fuck.balance.LoadBalance;
import top.huzhurong.fuck.proxy.ProviderSet;
import top.huzhurong.fuck.register.zk.ZkRegister;
import top.huzhurong.fuck.serialization.ISerialization;
import top.huzhurong.fuck.serialization.SerializationFactory;
import top.huzhurong.fuck.transaction.Client;
import top.huzhurong.fuck.transaction.netty.request.NettyClient;
import top.huzhurong.fuck.transaction.support.ChannelMap;
import top.huzhurong.fuck.transaction.support.Consumer;
import top.huzhurong.fuck.transaction.support.Provider;
import top.huzhurong.fuck.transaction.support.Request;
import top.huzhurong.fuck.util.NetUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.UUID;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/1
 */
@Getter
@Setter
@Slf4j
public class ReferenceBean implements FactoryBean, InitializingBean, ApplicationContextAware {
    private String id;
    private String interfaceName;
    private String version;
    private Integer timeout = 10000;
    private Object object;
    private String loadBalance;

    private ApplicationContext applicationContext;


    @Override
    public Object getObject() {
        return object;
    }

    @Override
    public Class<?> getObjectType() {
        return object.getClass();
    }

    @Override
    public void afterPropertiesSet() throws ClassNotFoundException {
        this.build();
    }

    private void build() throws ClassNotFoundException {
        //引用服务
        ZkRegister zkRegister = applicationContext.getBean(ZkRegister.class);
        buildConsumer(zkRegister);
        List<Provider> discover = zkRegister.discover(this.interfaceName, this.version);
        if (discover == null || discover.size() == 0) {
            throw new RuntimeException("服务端列表[" + this.interfaceName + "--" + this.version + "]为空");
        }
        ProviderSet.put(this.interfaceName, discover);
        Object object = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{Class.forName(this.interfaceName)}
                , new FuckRpcInvocationHandler(this));
        Assert.notNull(object, "代理引用不能为空");
        this.object = object;
    }

    private void buildConsumer(ZkRegister zkRegister) {
        Consumer consumer = new Consumer();
        consumer.setHost(NetUtils.getLocalHost());
        consumer.setTimeout(this.timeout);
        consumer.setVersion(this.version);
        consumer.setServiceName(this.interfaceName);
        zkRegister.registerConsumer(consumer);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    class FuckRpcInvocationHandler implements InvocationHandler {

        private String className;
        private String version;
        private LoadBalance loadBalance;
        private ISerialization serialization;

        public FuckRpcInvocationHandler(ReferenceBean referenceBean) {
            Assert.notNull(referenceBean, "referenceBean 不能为空");
            this.className = referenceBean.getInterfaceName();
            this.version = referenceBean.getVersion();
            loadBalance = LoadBalanceFactory.resolve(referenceBean.getLoadBalance());
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            List<Provider> all = ProviderSet.getAll(this.className);
            if (all == null || all.size() == 0) {
                throw new RuntimeException("服务端列表[" + this.className + "--" + this.version + "]为空");
            }
            Provider provider = loadBalance.getProvider(all);
            {
                serialization = SerializationFactory.resolve(provider.getSerialization());
            }
            String host = provider.getHost();
            String serviceName = provider.getServiceName();
            String version = provider.getVersion();
            String info = host + ":" + serviceName + ":" + version;
            SocketChannel channel = ChannelMap.get(info);
            if (channel == null) {
                //链接客户端
                Client client = new NettyClient(provider, this.serialization);
                client.connect(host, provider.getPort());
                Thread.sleep(300L);
            }
            if (channel == null) {
                channel = ChannelMap.get(info);
            }
            Assert.notNull(channel, "通道不能为空");
            Request request = new Request();
            request.setRequestId(UUID.randomUUID().toString());
            request.setServiceName(provider.getServiceName());
            request.setArgs(args);
            request.setMethodName(method.getName());
            request.setParameters(method.getParameterTypes());
            ChannelFuture channelFuture = channel.writeAndFlush(request);
            channelFuture.addListener((GenericFutureListener<? extends Future<? super Void>>) future -> {
                boolean success = future.isSuccess();
                if (!success) {
                    Throwable cause = future.cause();
                    cause.printStackTrace();
                    System.out.println("gg");
                }
            });
            return "111mm";
        }
    }
}
