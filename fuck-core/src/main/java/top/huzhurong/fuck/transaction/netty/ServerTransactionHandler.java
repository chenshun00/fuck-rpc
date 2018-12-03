package top.huzhurong.fuck.transaction.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import top.huzhurong.fuck.transaction.support.Request;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/2
 */
@ChannelHandler.Sharable
public class ServerTransactionHandler extends SimpleChannelInboundHandler<Serializable> {

    private ExecutorService responseTask = Executors.newFixedThreadPool(1);

    private ApplicationContext applicationContext;

    public ServerTransactionHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("channelActive:" + ctx.channel().toString());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("channelInactive:" + ctx.channel().toString());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Serializable serializable) {
        if (serializable instanceof Request) {
            Request request = (Request) serializable;
            responseTask.execute(new ResponseTask(request, channelHandlerContext, this.applicationContext));
        }
    }
}
