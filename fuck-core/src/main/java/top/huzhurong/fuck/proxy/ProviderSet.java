package top.huzhurong.fuck.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import top.huzhurong.fuck.transaction.support.Provider;

import java.util.*;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/1
 */
@Slf4j
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
            return new ArrayList<>();
        }
        return providers;
    }

    public static void reset(String service, List<Provider> collect) {
        Assert.notNull(service, "服务不能为空");
        stringListMap.remove(service);
        if (CollectionUtils.isEmpty(collect)) {
            log.warn("all provider has bean shutdown [{}]", service);
            return;
        }
        put(service, collect);
    }
}
