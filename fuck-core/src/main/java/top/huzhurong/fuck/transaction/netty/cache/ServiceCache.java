package top.huzhurong.fuck.transaction.netty.cache;

import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/5
 */
public class ServiceCache {
    private static Map<String, Object> serviceMap = new ConcurrentHashMap<>(32);

    public static void put(String key, Object value) {
        Assert.notNull(key, "服务不能为空");
        Assert.notNull(value, "服务对象不能为空");
        if (serviceMap.get(key) == null) {
            serviceMap.put(key, value);
        } else {
            throw new RuntimeException("duplicate 服务");
        }
    }

    public static Object getService(String service) {
        Assert.notNull(service, "服务不能为空");
        return serviceMap.get(service);
    }

}
