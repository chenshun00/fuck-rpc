package top.huzhurong.fuck.spring.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import top.huzhurong.fuck.balance.LoadBalance;
import top.huzhurong.fuck.filter.FuckFilter;
import top.huzhurong.fuck.filter.FuckFilterManager;
import top.huzhurong.fuck.filter.annotation.FuckFilterChain;
import top.huzhurong.fuck.proxy.ProviderSet;
import top.huzhurong.fuck.register.zk.ZkRegister;
import top.huzhurong.fuck.transaction.invoker.ClientInvoker;
import top.huzhurong.fuck.transaction.support.*;
import top.huzhurong.fuck.util.NetUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/1
 */
@Getter
@Setter
@Slf4j
public class ReferenceBean implements FactoryBean, InitializingBean, ApplicationContextAware, Serializable {
    private String id;
    private String interfaceName;
    private String version;
    private Integer timeout = 10;
    private Object object;
    private String loadBalance;

    private AtomicBoolean register = new AtomicBoolean(false);

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
        zkRegister.subscribe(this.interfaceName);
        ProviderSet.put(this.interfaceName, discover);
        Object object = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{Class.forName(this.interfaceName)}
                , new FuckRpcInvocationHandler(this));
        Assert.notNull(object, "代理引用不能为空");
        this.object = object;
    }

    private void buildConsumer(ZkRegister zkRegister) {
        if (register.compareAndSet(false, true)) {
            Consumer consumer = new Consumer();
            consumer.setHost(NetUtils.getLocalHost());
            consumer.setTimeout(this.timeout);
            consumer.setVersion(this.version);
            consumer.setServiceName(this.interfaceName);
            zkRegister.registerConsumer(consumer);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    class FuckRpcInvocationHandler implements InvocationHandler {

        private String className;
        private String version;
        private LoadBalance loadBalance;
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
            if (CollectionUtils.isEmpty(all)) {
                throw new RuntimeException("服务端列表[" + this.className + "--" + this.version + "]为空");
            }
            Provider provider = loadBalance.getProvider(all);
            Request request = Request.buildRequest(provider, method, args, timeout);
            request.setAsync(method.getReturnType().isAssignableFrom(CompletableFuture.class));
            List<FuckFilter> fuckFilters = FuckFilterManager.instance.getConsumerFilter();
            FuckFilterChain fuckFilterChain = new FuckFilterChain(fuckFilters, new ClientInvoker(request));
            return fuckFilterChain.doNext(request, null);
        }
    }
}
