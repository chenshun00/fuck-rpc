package top.huzhurong.fuck.transaction.support;

import lombok.extern.slf4j.Slf4j;
import top.huzhurong.fuck.transaction.netty.future.ResponseFuture;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 根据引用的服务信息从zk上找到provider列表，然后建立tcp长链接
 * <p>
 * 我们是serviceName 去代表channel呢还是怎么去代表channel
 * 首先一个channel代表是一个服务的通道，所有我们需要找出所有的共同的service---host:port
 *
 * @author chenshun00@gmail.com
 * @since 2018/11/30
 */
@Slf4j
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


    private static final ConcurrentMap<String /* opaque */, ResponseFuture> responseTable = new ConcurrentHashMap<>(256);
    public static final ConcurrentMap<String /* opaque */, CompletableFuture<Object>> asyncTable = new ConcurrentHashMap<>(256);

    public static ResponseFuture putResponseFuture(final String requestId, final ResponseFuture responseFuture) {
        ResponseFuture put = responseTable.put(requestId, responseFuture);
        if (put != null) {
            log.warn("what hell,duplicate requestId");
        }
        return responseFuture;
    }

    public static ResponseFuture getResponseFuture(final String requestId) {
        return responseTable.get(requestId);
    }

//    static {
//        ScheduledExecutorService service = Executors.newScheduledThreadPool(1, new FuckThreadFactory("scan-responseTable-"));
//        service.scheduleAtFixedRate(() -> {
//            Iterator<Map.Entry<String, ResponseFuture>> iterator = responseTable.entrySet().iterator();
//            while (iterator.hasNext()) {
//                Map.Entry<String, ResponseFuture> next = iterator.next();
//                ResponseFuture value = next.getValue();
//                long beginTimestamp = value.getBeginTimestamp();
//                long timeout = value.getTimeout();
//                long timeMillis = System.currentTimeMillis();
//                if ((beginTimestamp + timeout + 1000) <= timeMillis) {
//                    iterator.remove();
//                }
//            }
//        }, 10, 10, TimeUnit.SECONDS);
//    }

}
