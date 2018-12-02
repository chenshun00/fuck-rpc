package top.huzhurong.fuck.proxy;

import top.huzhurong.fuck.transaction.support.Provider;

import java.util.*;

/**
 * @author luobo.cs@raycloud.com
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
}
