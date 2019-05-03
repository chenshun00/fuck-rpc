package top.huzhurong.fuck.spring.bean;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import top.huzhurong.fuck.register.zk.ZkRegister;

import java.io.Serializable;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/1
 */
public class AddressBean implements FactoryBean<ZkRegister>, InitializingBean,Serializable {

    private String id;
    private ZkRegister zkRegister;
    private Integer session;
    private Integer connection;
    private String address;

    @Override
    public ZkRegister getObject() {
        return this.zkRegister;
    }

    @Override
    public Class<?> getObjectType() {
        return ZkRegister.class;
    }

    @Override
    public void afterPropertiesSet() {
        this.zkRegister = build();
    }

    private ZkRegister build() {
        if (session != null && connection != null) {
            zkRegister = new ZkRegister(address, session, connection);
        } else {
            zkRegister = new ZkRegister(address);
        }
        return zkRegister;
    }

    public ZkRegister getZkRegister() {
        return zkRegister;
    }

    public void setZkRegister(ZkRegister zkRegister) {
        this.zkRegister = zkRegister;
    }

    public Integer getSession() {
        return session;
    }

    public void setSession(Integer session) {
        this.session = session;
    }

    public Integer getConnection() {
        return connection;
    }

    public void setConnection(Integer connection) {
        this.connection = connection;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
