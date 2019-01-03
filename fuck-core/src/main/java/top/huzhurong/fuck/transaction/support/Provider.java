package top.huzhurong.fuck.transaction.support;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/1
 */
@Getter
@Setter
@ToString
public class Provider implements Serializable, Cloneable {
    private String serviceName;
    private String host;
    private Integer port;
    private String version;
    private Integer weight;
    private String serialization;

    public Provider() {
    }

    @Override
    public Provider clone() throws CloneNotSupportedException {
        return (Provider) super.clone();
    }

    public String buildIfno() {
        return getHost() + ":" + getPort() + ":" + getServiceName() + ":" + getVersion();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Provider)) return false;

        Provider provider = (Provider) o;

        if (!getServiceName().equals(provider.getServiceName())) return false;
        if (!getHost().equals(provider.getHost())) return false;
        if (!getPort().equals(provider.getPort())) return false;
        if (!getVersion().equals(provider.getVersion())) return false;
        if (!getWeight().equals(provider.getWeight())) return false;
        return getSerialization() != null ? getSerialization().equals(provider.getSerialization()) : provider.getSerialization() == null;
    }

    @Override
    public int hashCode() {
        int result = getServiceName().hashCode();
        result = 31 * result + getHost().hashCode();
        result = 31 * result + getPort().hashCode();
        result = 31 * result + getVersion().hashCode();
        result = 31 * result + getWeight().hashCode();
        result = 31 * result + (getSerialization() != null ? getSerialization().hashCode() : 0);
        return result;
    }
}
