package top.huzhurong.fuck.transaction.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 根据引用的服务信息从zk上找到provider列表，然后建立tcp长链接
 * <p>
 * 我们是serviceName 去代表channel呢还是怎么去代表channel
 * 首先一个channel代表是一个服务的通道，所有我们需要找出所有的共同的service---host:port
 *
 * @author chenshun00@gmail.com
 * @since 2018/11/30
 */
public class TempResultSet {

    public static ThreadFactory defaultThreadFactory() {
        return new FuckThreadFactory();
    }

    public static ExecutorService executorService = Executors.newFixedThreadPool(1, defaultThreadFactory());

    private static Map<String, Response> serverMap = new ConcurrentHashMap<>(32);

    public static void put(String path, Response response) {
        serverMap.put(path, response);
    }

    public static Response get(String path) {
        return serverMap.remove(path);
    }


}
