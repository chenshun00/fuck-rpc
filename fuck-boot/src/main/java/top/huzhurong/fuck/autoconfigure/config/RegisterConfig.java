package top.huzhurong.fuck.autoconfigure.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenshun00@gmail.com
 * @since 2019/5/3
 */
@Configuration
@ConditionalOnProperty(prefix = "fuck.registry", name = "address", matchIfMissing = true)
public class RegisterConfig {
    private String id;
    private String address;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
