package top.huzhurong.fuck.transaction.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import org.springframework.util.Assert;
import top.huzhurong.fuck.transaction.support.ChannelMap;
import top.huzhurong.fuck.transaction.support.Provider;
import top.huzhurong.fuck.transaction.support.Response;
import top.huzhurong.fuck.transaction.support.TempResultSet;

import java.io.Serializable;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/11/30
 */
@ChannelHandler.Sharable
public class ClientTransactionHandler extends SimpleChannelInboundHandler<Serializable> {

    private Provider provider;

    public ClientTransactionHandler(Provider provider) {
        this.provider = provider;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        String info = this.provider.getHost() + ":" + this.provider.getServiceName() + ":" + this.provider.getVersion();
        ChannelMap.remove(info);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Serializable serializable) {
        if (serializable instanceof Response) {
            Response response = (Response) serializable;
            TempResultSet.put(response.getRequestId(), response);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Assert.notNull(this.provider, "服务提供者不能为空");
        String info = this.provider.getHost() + ":" + this.provider.getServiceName() + ":" + this.provider.getVersion();
        ChannelMap.put(info, (SocketChannel) ctx.channel());
    }


}
