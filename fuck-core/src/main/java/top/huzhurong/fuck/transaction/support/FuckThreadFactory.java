package top.huzhurong.fuck.transaction.support;

import org.springframework.scheduling.concurrent.DefaultManagedAwareThreadFactory;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/4
 */
public class FuckThreadFactory extends DefaultManagedAwareThreadFactory {

    public FuckThreadFactory() {
        this.setDaemon(true);
        this.setThreadNamePrefix("fuck-rpc-");
    }
}
