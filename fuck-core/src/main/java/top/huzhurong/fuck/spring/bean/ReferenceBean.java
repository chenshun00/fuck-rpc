package top.huzhurong.fuck.spring.bean;

import io.netty.channel.socket.SocketChannel;
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
import top.huzhurong.fuck.transaction.support.*;
import top.huzhurong.fuck.util.NetUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
    private Integer timeout = 10;
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
        private Integer timeout;

        FuckRpcInvocationHandler(ReferenceBean referenceBean) {
            Assert.notNull(referenceBean, "referenceBean 不能为空");
            this.className = referenceBean.getInterfaceName();
            this.version = referenceBean.getVersion();
            this.timeout = referenceBean.getTimeout();
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
                serialization = SerializationFactory.resolve(provider.getSerialization(),this.className);
            }
            String host = provider.getHost();
            String serviceName = provider.getServiceName();
            String version = provider.getVersion();
            String info = host + ":" + serviceName + ":" + version;
            SocketChannel channel = ChannelMap.get(info);
            if (channel == null) {
                Client client = new NettyClient(provider, this.serialization);
                client.connect(host, provider.getPort());
                Thread.sleep(200L);
            }
            if (channel == null) {
                channel = ChannelMap.get(info);
            }

            SocketChannel finalChannel = channel;
            Future<Response> submit = TempResultSet.executorService.submit(() -> {
                Assert.notNull(finalChannel, "通道不能为空");
                Request request = new Request();
                request.setRequestId(UUID.randomUUID().toString());
                request.setServiceName(provider.getServiceName());
                request.setArgs(args);
                request.setMethodName(method.getName());
                request.setParameters(method.getParameterTypes());
                finalChannel.writeAndFlush(request);
                for (; ; ) {
                    Response response = TempResultSet.get(request.getRequestId());
                    if (response != null) {
                        return response;
                    }
                }
            });

            try {
                Response response = submit.get(this.timeout, TimeUnit.SECONDS);
                if (response == null) {
                    throw new RuntimeException("好像出现了未知的异常");
                }
                if (response.getSuccess()) {
                    return response.getObject();
                }
                Throwable exception = response.getException();
                if (exception != null) {
                    throw new RuntimeException(exception);
                }
                return null;
            } catch (TimeoutException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
