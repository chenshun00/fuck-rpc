package top.huzhurong.fuck.spring.bean;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/2
 */
public class PortBean implements FactoryBean<ProtocolPort>, InitializingBean {

    private Integer port;
    private ProtocolPort protocolPort;

    @Override
    public void afterPropertiesSet() {
        protocolPort = new ProtocolPort();
        protocolPort.setPort(port);
    }

    @Override
    public ProtocolPort getObject() {
        return this.protocolPort;
    }

    @Override
    public Class<?> getObjectType() {
        return ProtocolPort.class;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public ProtocolPort getProtocolPort() {
        return protocolPort;
    }

    public void setProtocolPort(ProtocolPort protocolPort) {
        this.protocolPort = protocolPort;
    }
}
