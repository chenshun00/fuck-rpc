package top.huzhurong.fuck.transaction.support;

import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/1
 */
@Slf4j
public class ChannelMap {

    private static boolean is_info = log.isInfoEnabled();

    private static ScheduledExecutorService HEART_SCHEDULE_SERVICE = Executors.newSingleThreadScheduledExecutor(new DefaultThreadFactory("heart"));

    private static Map<String, SocketChannel> channelMap = new HashMap<>();

    public static void put(String info, SocketChannel socketChannel) {
        channelMap.put(info, socketChannel);
    }


    public static SocketChannel get(String info) {
        return channelMap.get(info);
    }

    public static void remove(String info) {
        channelMap.remove(info);
    }

    static {
        HEART_SCHEDULE_SERVICE.scheduleAtFixedRate(() -> channelMap.forEach((k, socketChannel) -> {
            final Request request = new Request();
            request.setRequestId(UUID.randomUUID().toString().replace("-", ""));
            request.setCode(2);
            request.setAsync(true);
            ChannelFuture write = socketChannel.writeAndFlush(request);
            write.addListener(future -> {
                if (is_info) {
                    log.info("send heart to {}", k);
                }
                CompletableFuture<Object> completableFuture = new CompletableFuture<>();
                TempResultSet.asyncTable.put(request.getRequestId(), completableFuture);
                completableFuture.whenComplete((result, exception) -> log.info("receive result [{}] [{}]", result, socketChannel));
            });
        }), 10, 10, TimeUnit.SECONDS);
    }

}
