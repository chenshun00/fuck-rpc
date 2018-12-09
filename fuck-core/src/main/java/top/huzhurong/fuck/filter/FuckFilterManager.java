package top.huzhurong.fuck.filter;

import top.huzhurong.fuck.filter.annotation.Consumer;
import top.huzhurong.fuck.filter.annotation.Provider;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/4
 */
public class FuckFilterManager {

    //不初始化就不用分配内存
    private static List<FuckFilter> providerFilter = new ArrayList<>();
    private static List<FuckFilter> consumerFilter = new ArrayList<>();

    static {
        ServiceLoader<FuckFilter> operations = ServiceLoader.load(FuckFilter.class);
        for (FuckFilter operation : operations) {
            boolean provider = false, consumer = false;
            if (operation.getClass().isAnnotationPresent(Provider.class)) {
                provider = true;
                providerFilter.add(operation);
            }
            if (operation.getClass().isAnnotationPresent(Consumer.class)) {
                consumer = true;
                consumerFilter.add(operation);
            }
            if (!provider && !consumer) {
                providerFilter.add(operation);
                consumerFilter.add(operation);
            }
        }
    }

    private FuckFilterManager() {
    }

    public final static FuckFilterManager instance = new FuckFilterManager();

    public List<FuckFilter> getConsumerFilter() {
        return consumerFilter;
    }

    public List<FuckFilter> getProviderFilter() {
        return providerFilter;
    }
}
