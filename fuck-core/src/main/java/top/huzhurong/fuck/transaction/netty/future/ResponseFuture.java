package top.huzhurong.fuck.transaction.netty.future;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import top.huzhurong.fuck.transaction.support.Response;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author chenshun00@gmail.com
 * @since 2019/4/30
 */
@Getter
@Setter
public class ResponseFuture {
    private final String requestId;
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private final Channel processChannel;
    private final long beginTimestamp = System.currentTimeMillis();
    private volatile boolean sendRequestOK = true;
    private volatile Throwable cause;
    private Response response;
    private CompletableFuture<Object> future;
    private long timeout;
    private Boolean async;

    public ResponseFuture(Channel channel, String requestId, long timeout, CompletableFuture<Object> future) {
        this.requestId = requestId;
        this.processChannel = channel;
        this.timeout = timeout;
        this.future = future;
        async = future != null;
    }

    public Response waitForRepsonse() throws InterruptedException {
        countDownLatch.await(timeout, TimeUnit.SECONDS);
        return response;
    }

    public void putResponse(final Response response) {
        this.response = response;
        countDownLatch.countDown();
    }
}
