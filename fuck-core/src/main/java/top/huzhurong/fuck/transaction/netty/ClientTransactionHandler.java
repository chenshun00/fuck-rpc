package top.huzhurong.fuck.transaction.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import org.springframework.util.Assert;
import top.huzhurong.fuck.transaction.support.ChannelMap;
import top.huzhurong.fuck.transaction.support.Provider;
import top.huzhurong.fuck.transaction.support.Request;
import top.huzhurong.fuck.transaction.support.Response;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/11/30
 */
@ChannelHandler.Sharable
public class ClientTransactionHandler extends SimpleChannelInboundHandler<Serializable> {

    private ExecutorService requestTask = Executors.newFixedThreadPool(5);

    private Provider provider;

    public ClientTransactionHandler(Provider provider) {
        this.provider = provider;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("发生什么");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Serializable serializable) throws Exception {
        if (serializable instanceof Response) {
            Response response = (Response) serializable;
            requestTask.submit(new RequestTask(response));
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Assert.notNull(this.provider, "服务提供者不能为空");
        //放置到一个Map里边，这个Map负载监控channel
        System.out.println("here");
        String info = this.provider.getHost() + ":" + this.provider.getServiceName() + ":" + this.provider.getVersion();
        ChannelMap.put(info, (SocketChannel) ctx.channel());
    }


}
