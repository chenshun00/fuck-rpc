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

    private static Map<String, Response> serverMap = new ConcurrentHashMap<>(32);

    public static void put(String path, Response response) {
        serverMap.put(path, response);
    }

    public static Response get(String path) {
        return serverMap.remove(path);
    }


    private static final ConcurrentMap<String /* requestId */, ResponseFuture> responseTable = new ConcurrentHashMap<>(256);

    public static ResponseFuture putResponseFuture(final String requestId, final ResponseFuture responseFuture) {
        ResponseFuture put = responseTable.put(requestId, responseFuture);
        if (put != null) {
            log.warn("what hell,duplicate requestId");
        }
        return responseFuture;
    }

    public static ResponseFuture getResponseFuture(final String requestId) {
        return responseTable.remove(requestId);
    }

    static {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1, new FuckThreadFactory("scan-responseTable-"));
        service.scheduleAtFixedRate(() -> {
            Iterator<Map.Entry<String, ResponseFuture>> iterator = responseTable.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ResponseFuture> next = iterator.next();
                ResponseFuture value = next.getValue();
                //1500000 +1000 = 1501000 <== 16000000
                long beginTimestamp = value.getBeginTimestamp();
                long timeout = value.getTimeout();
                if ((beginTimestamp + timeout * 10 + 1000) <= System.currentTimeMillis()) {
                    iterator.remove();
                }
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

}
