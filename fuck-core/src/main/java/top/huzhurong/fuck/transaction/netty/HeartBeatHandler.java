package top.huzhurong.fuck.transaction.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
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
public class HeartBeatHandler extends SimpleChannelInboundHandler<Serializable> {

    private ExecutorService responseTask = Executors.newFixedThreadPool(10);
    private ExecutorService requestTask = Executors.newFixedThreadPool(5);

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        //激活的通道有了
        Channel channel = ctx.channel();
        //放置到一个Map里边，这个Map负载监控channel
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Serializable serializable) throws Exception {
        if (serializable instanceof Request) {
            Request request = (Request) serializable;
            responseTask.submit(new ResponseTask(request, channelHandlerContext));
        } else {
            Response response = (Response) serializable;
            requestTask.submit(new RequestTask(response));
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }
}
