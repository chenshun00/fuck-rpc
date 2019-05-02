package top.huzhurong.fuck.transaction.invoker;

import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.SocketChannel;
import org.springframework.util.Assert;
import top.huzhurong.fuck.serialization.SerializationFactory;
import top.huzhurong.fuck.transaction.Client;
import top.huzhurong.fuck.transaction.netty.future.ResponseFuture;
import top.huzhurong.fuck.transaction.netty.request.NettyClient;
import top.huzhurong.fuck.transaction.support.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/9
 */
public class ClientInvoker extends Invoker {

    public ClientInvoker(Request request) {
        super(request);
    }

    @Override
    public Object invoke() {
        Assert.notNull(request, "request 不能为空!");

        String info = request.getProvider().buildIfno();
        AtomicReference<SocketChannel> channel = new AtomicReference<>(ChannelMap.get(info));
        Provider provider = request.getProvider();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        if (channel.get() == null) {

            Client client = new NettyClient(provider, SerializationFactory.resolve(request.getSerialization(), request.getServiceName()));
            ChannelFuture channelFuture = client.connect(provider.getHost(), provider.getPort());
            channelFuture.addListener(future -> {
                if (future.isSuccess()) {
                    channel.set(ChannelMap.get(info));
                    countDownLatch.countDown();
                }
            });
        }
        if (channel.get() == null) {
            try {
                countDownLatch.await(10000, TimeUnit.MICROSECONDS);
            } catch (Exception e) {
                throw new RuntimeException("链接远程服务" + provider.getHost() + ":" + provider.getPort() + "失败");
            }
            if (channel.get() == null) {
                throw new RuntimeException("链接远程服务" + provider.getHost() + ":" + provider.getPort() + "失败");
            }
        }
        write(channel);
        if (!request.getAsync()) {
            try {
                ResponseFuture responseFuture = TempResultSet.putResponseFuture(request.getRequestId(), new ResponseFuture(channel.get(), request.getRequestId(), request.getTimeout()));
                Response response = responseFuture.waitForRepsonse();
                if (response == null) {
                    throw new RuntimeException("timeout exception:" + request.getTimeout());
                }
                if (response.getSuccess()) {
                    return response.getObject();
                }
                Throwable exception = response.getException();
                if (exception != null) {
                    throw new RuntimeException(exception);
                }
                throw new RuntimeException("unknown exception");
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            CompletableFuture<Object> completableFuture = new CompletableFuture<>();
            TempResultSet.asyncTable.put(request.getRequestId(), completableFuture);
            return completableFuture;
        }
    }

    private ChannelFuture write(AtomicReference<SocketChannel> channel) {
        SocketChannel socketChannel = channel.get();
        return socketChannel.writeAndFlush(request);
    }
}
