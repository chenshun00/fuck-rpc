package top.huzhurong.fuck.autoconfigure.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenshun00@gmail.com
 * @since 2019/5/3
 */
@Configuration
@ConditionalOnProperty(prefix = "fuck.protocol", name = "port",matchIfMissing = true)
public class ProtocolConfig {
    private String id;
    private Integer port;

    public String getId() {
        return id;
    }

    @ConditionalOnProperty(prefix = "fuck.protocol", name = "id",matchIfMissing = true)
    public void setId(String id) {
        this.id = id;
    }

    public Integer getPort() {
        return port;
    }

    @ConditionalOnProperty(prefix = "fuck.protocol", name = "port",matchIfMissing = true)
    public void setPort(Integer port) {
        this.port = port;
    }
}
