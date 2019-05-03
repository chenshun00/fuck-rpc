package top.huzhurong.fuck.spring.bean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;
import top.huzhurong.fuck.register.IRegister;
import top.huzhurong.fuck.register.zk.ZkRegister;
import top.huzhurong.fuck.serialization.ISerialization;
import top.huzhurong.fuck.serialization.SerializationFactory;
import top.huzhurong.fuck.transaction.Server;
import top.huzhurong.fuck.transaction.netty.request.NettyServer;
import top.huzhurong.fuck.transaction.support.Provider;
import top.huzhurong.fuck.util.NetUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 单个服务发布
 *
 * @author chenshun00@gmail.com
 * @since 2018/12/1
 */
public class ServiceBean implements InitializingBean, ApplicationContextAware, Serializable {

    private String id;
    private String interfaceName;
    private String version;
    private Integer weight;
    private IRegister register;
    private String serialization;
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
        ISerialization serialization = SerializationFactory.resolve(this.serialization, this.interfaceName);
        List<Provider> providerList = new ArrayList<>(16);
        Provider provider = new Provider();
        provider.setSerialization(serialization.toString());
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
        Server server = new NettyServer(serialization, this.applicationContext);
        server.bind(this.protocolPort.getPort());
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public IRegister getRegister() {
        return register;
    }

    public void setRegister(IRegister register) {
        this.register = register;
    }

    public String getSerialization() {
        return serialization;
    }

    public void setSerialization(String serialization) {
        this.serialization = serialization;
    }

    public Object getImpl() {
        return impl;
    }

    public void setImpl(Object impl) {
        this.impl = impl;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public ProtocolPort getProtocolPort() {
        return protocolPort;
    }

    public void setProtocolPort(ProtocolPort protocolPort) {
        this.protocolPort = protocolPort;
    }
}
