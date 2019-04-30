package top.huzhurong.fuck.transaction.netty;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import top.huzhurong.fuck.transaction.netty.future.ResponseFuture;
import top.huzhurong.fuck.transaction.support.ChannelMap;
import top.huzhurong.fuck.transaction.support.Provider;
import top.huzhurong.fuck.transaction.support.Response;
import top.huzhurong.fuck.transaction.support.TempResultSet;

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
        if (log.isErrorEnabled()) {
            log.warn("连接断开:{}", this.provider);
        }
        ChannelMap.remove(info);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Serializable serializable) {
        if (serializable instanceof Response) {
            Response response = (Response) serializable;
            if (response.getAsync()) {
                CompletableFuture<Object> objectCompletableFuture = TempResultSet.asyncTable.get(response.getRequestId());
                if (objectCompletableFuture != null) {
                    if (response.getObject() != null) {
                        objectCompletableFuture.complete(response.getObject());
                    } else {
                        assert response.getException() != null;
                        objectCompletableFuture.completeExceptionally(response.getException());
                    }
                }
            } else {
                ResponseFuture responseFuture = TempResultSet.getResponseFuture(response.getRequestId());
                if (responseFuture != null) {
                    responseFuture.putResponse(response);
                }
            }
        }
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
