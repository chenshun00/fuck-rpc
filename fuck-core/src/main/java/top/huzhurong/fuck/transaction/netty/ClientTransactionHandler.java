package top.huzhurong.fuck.transaction.netty;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import top.huzhurong.fuck.transaction.netty.future.ResponseFuture;
import top.huzhurong.fuck.transaction.support.*;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

/**
 * @author chenshun00@gmail.com
 * @since 2018/11/30
 */
@Slf4j
@Sharable
public class ClientTransactionHandler extends SimpleChannelInboundHandler<Serializable> {

    private Provider provider;

    public ClientTransactionHandler(Provider provider) {
        this.provider = provider;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        String info = this.provider.buildIfno();
        if (log.isInfoEnabled()) {
            log.info("连接断开:{}", this.provider);
        }
        ChannelMap.remove(info);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Serializable serializable) {
        if (serializable instanceof Response) {
            Response response = (Response) serializable;
            ResponseFuture responseFuture = TempResultSet.getResponseFuture(response.getRequestId());
            if (responseFuture == null) {
                return;
            }
            if (response.getAsync()) {
                CompletableFuture<Object> future = responseFuture.getFuture();
                if (future != null) {
                    if (response.getObject() != null) {
                        future.complete(response.getObject());
                    } else {
                        assert response.getException() != null;
                        future.completeExceptionally(response.getException());
                    }
                }
            } else {
                responseFuture.putResponse(response);
            }
        }
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Assert.notNull(this.provider, "服务提供者不能为空");
        String info = this.provider.buildIfno();
        if (log.isInfoEnabled()) {
            log.info("服务成功连接到:{}", this.provider);
        }
        ChannelMap.put(info, (SocketChannel) ctx.channel());
    }


}
