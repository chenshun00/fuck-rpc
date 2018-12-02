package top.huzhurong.fuck.transaction.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.huzhurong.fuck.transaction.support.Request;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/2
 */
public class ServerTransactionHandler extends SimpleChannelInboundHandler<Serializable> {

    private ExecutorService responseTask = Executors.newFixedThreadPool(1);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Serializable serializable) {
        if (serializable instanceof Request) {
            System.out.println("Request:" + serializable);
            Request request = (Request) serializable;
            responseTask.submit(new ResponseTask(request, channelHandlerContext));
        }
    }
}
