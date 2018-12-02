package top.huzhurong.fuck.transaction.support;

import java.io.Serializable;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/2
 */
public class Consumer implements Serializable {
    private String host;
    private String serviceName;
    private String version;
    private Integer timeout;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
}
