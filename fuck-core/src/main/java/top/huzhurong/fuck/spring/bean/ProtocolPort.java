package top.huzhurong.fuck.spring.bean;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/2
 */
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolPort {
    private Integer port;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
