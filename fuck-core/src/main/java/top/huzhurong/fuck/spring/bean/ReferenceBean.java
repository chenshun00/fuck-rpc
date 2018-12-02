package top.huzhurong.fuck.spring.bean;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import top.huzhurong.fuck.balance.LoadBalance;
import top.huzhurong.fuck.balance.RoundRobin;
import top.huzhurong.fuck.proxy.ProviderSet;
import top.huzhurong.fuck.register.zk.ZkRegister;
import top.huzhurong.fuck.transaction.support.ChannelMap;
import top.huzhurong.fuck.transaction.support.Provider;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/1
 */
@Getter
@Setter
public class ReferenceBean implements FactoryBean, InitializingBean, ApplicationContextAware {
    private String id;
    private String interfaceName;
    private String version;
    private Integer timeout = 10;
    private Object object;

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
        List<Provider> discover = zkRegister.discover(this.interfaceName, this.version);
        if (discover == null || discover.size() == 0) {
            throw new RuntimeException("服务端列表[" + this.interfaceName + "--" + this.version + "]为空");
        }
        ProviderSet.put(this.interfaceName, discover);
        Object object = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{Class.forName(this.interfaceName)}
                , new FuckRpcInvocationHandler(this.interfaceName, this.version));
        Assert.notNull(object, "代理引用不能为空");
        this.object = object;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    class FuckRpcInvocationHandler implements InvocationHandler {

        private String className;
        private String version;
        private LoadBalance loadBalance = new RoundRobin();

        public FuckRpcInvocationHandler(String className, String version) {
            Assert.notNull(className, "className 不能为空");
            Assert.notNull(version, "version 不能为空");
            this.className = className;
            this.version = version;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            List<Provider> all = ProviderSet.getAll(this.className);
            if (all == null || all.size() == 0) {
                throw new RuntimeException("服务端列表[" + this.className + "--" + this.version + "]为空");
            }
            Provider provider = loadBalance.getProvider(all);
            String host = provider.getHost();
            String serviceName = provider.getServiceName();
            String version = provider.getVersion();
            String info = host + ":" + serviceName + ":" + version;
            Channel channel = ChannelMap.get(info);
            if (channel == null) {
                throw new RuntimeException("服务丢失");
            }
            ChannelFuture channelFuture = channel.writeAndFlush(null);
            boolean await = channelFuture.await(timeout, TimeUnit.SECONDS);
            return "111mm";
        }
    }
}
