package top.huzhurong.fuck.transaction.invoker;

import io.netty.channel.socket.SocketChannel;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import top.huzhurong.fuck.serialization.SerializationFactory;
import top.huzhurong.fuck.transaction.Client;
import top.huzhurong.fuck.transaction.netty.request.NettyClient;
import top.huzhurong.fuck.transaction.support.*;
import top.huzhurong.fuck.util.MethodUtils;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
        Request request = getRequest();
        Assert.notNull(request, "request 不能为空!");

        String info = request.getProvider().buildIfno();
        SocketChannel channel = ChannelMap.get(info);
        Provider provider = request.getProvider();
        if (channel == null) {
            Client client = new NettyClient(provider, SerializationFactory.resolve(request.getSerialization(), request.getServiceName()));
            client.connect(provider.getHost(), provider.getPort());
            channel = ChannelMap.get(info);
        }
        if (channel == null) {
            throw new RuntimeException("获取远程服务" + provider.getHost() + ":" + provider.getPort() + "失败");
        }

        SocketChannel finalChannel = channel;
        Future<Response> submit = TempResultSet.executorService.submit(() -> {
            Assert.notNull(finalChannel, "通道不能为空");
            //过滤的应该是插入这里才好吧
            finalChannel.writeAndFlush(request);
            if (MethodUtils.isVoid(request.getMethod())){
                return null;
            }
            //这里可以改造成 CountDownLatch,可以比 ;; 循环要好
            for (; ; ) {
                Response response = TempResultSet.get(request.getRequestId());
                if (response != null) {
                    return response;
                }
            }
        });
        if (MethodUtils.isVoid(request.getMethod())){
            return null;
        }

        try {
            Response response = submit.get(request.getTimeout(), TimeUnit.SECONDS);
            if (response == null) {
                //这里应该是超时了
                throw new RuntimeException("unknown exception");
            }
            if (response.getSuccess()) {
                return response.getObject();
            }
            Throwable exception = response.getException();
            if (exception != null) {
                throw new RuntimeException(exception);
            }
            throw new RuntimeException("unknown exception");
        } catch (TimeoutException | InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }
}
