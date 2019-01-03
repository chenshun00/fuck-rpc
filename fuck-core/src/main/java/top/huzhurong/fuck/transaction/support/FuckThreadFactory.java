package top.huzhurong.fuck.transaction.support;

import org.springframework.scheduling.concurrent.DefaultManagedAwareThreadFactory;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/4
 */
public class FuckThreadFactory extends DefaultManagedAwareThreadFactory {

    public FuckThreadFactory() {
        this.setDaemon(true);
        this.setThreadNamePrefix("fuck-rpc-");
    }
}
