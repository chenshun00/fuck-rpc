package top.huzhurong.fuck.transaction.support;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author luobo.cs@raycloud.com
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
}
