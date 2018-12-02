package top.huzhurong.fuck.spring.bean;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import top.huzhurong.fuck.register.IRegister;
import top.huzhurong.fuck.register.zk.ZkRegister;
import top.huzhurong.fuck.serialization.ISerialization;
import top.huzhurong.fuck.serialization.protobuff.ProtoBuffSerilize;
import top.huzhurong.fuck.transaction.Server;
import top.huzhurong.fuck.transaction.netty.NettyServer;
import top.huzhurong.fuck.transaction.support.Provider;
import top.huzhurong.fuck.util.NetUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 单个服务发布
 *
 * @author luobo.cs@raycloud.com
 * @since 2018/12/1
 */
@Getter
@Setter
public class ServiceBean implements FactoryBean<Object>, InitializingBean, ApplicationContextAware {

    private String id;
    private String interfaceName;
    private String version;
    private Integer weight;
    private IRegister register;
    private ISerialization serialization;
    private Object impl;


    private ApplicationContext applicationContext;
    private ProtocolPort protocolPort;

    @Override
    public void afterPropertiesSet() {
        this.build();
    }

    private void build() {
        if (register == null) {
            register = applicationContext.getBean(ZkRegister.class);
        }
        if (this.protocolPort == null) {
            this.protocolPort = this.applicationContext.getBean(ProtocolPort.class);
        }
        if (serialization == null) {
            serialization = new ProtoBuffSerilize();
        }
        List<Provider> providerList = new ArrayList<>(16);
        Provider provider = new Provider();
        provider.setServiceName(interfaceName);
        provider.setHost(NetUtils.getLocalHost());
        provider.setPort(this.protocolPort.getPort());
        if (weight == null) {
            provider.setWeight(1);
        } else {
            provider.setWeight(weight);
        }
        provider.setVersion(version);
        providerList.add(provider);
        //服务注册
        register.registerService(providerList);
        Server server = new NettyServer(serialization);
        server.bind(this.protocolPort.getPort());
    }


    @Override
    public Object getObject() {
        return this.impl;
    }

    @Override
    public Class<?> getObjectType() {
        return this.impl.getClass();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
