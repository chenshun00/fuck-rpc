package top.huzhurong.fuck.proxy;

import org.springframework.util.Assert;
import top.huzhurong.fuck.transaction.support.Provider;

import java.util.*;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/1
 */
public class ProviderSet {
    private static Map<String, List<Provider>> stringListMap = new HashMap<>(32);

    public static void put(String serviceName, List<Provider> providerList) {
        Objects.requireNonNull(serviceName);
        Objects.requireNonNull(providerList);

        List<Provider> providers = stringListMap.get(serviceName);
        if (providers == null) {
            providers = new ArrayList<>();
            stringListMap.put(serviceName, providers);
        } else {
            providers.clear();
        }
        providers.addAll(providerList);
    }

    public static List<Provider> getAll(String serviceName) {
        List<Provider> providers = stringListMap.get(serviceName);
        if (providers == null || providers.size() == 0) {
            throw new RuntimeException("provider 列表为空");
        }
        return providers;
    }

    public static void reset(String service, List<Provider> collect) {
        Assert.notNull(service, "服务不能为空");
        Assert.notEmpty(collect, "provider列表不能为空");
        stringListMap.remove(service);
        put(service, collect);
    }
}
