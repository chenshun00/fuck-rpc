package top.huzhurong.fuck.transaction.netty.request;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import top.huzhurong.fuck.serialization.ISerialization;
import top.huzhurong.fuck.transaction.Client;
import top.huzhurong.fuck.transaction.netty.ClientTransactionHandler;
import top.huzhurong.fuck.transaction.netty.serilize.MessageDecoder;
import top.huzhurong.fuck.transaction.netty.serilize.MessageEncoder;
import top.huzhurong.fuck.transaction.support.Provider;
import top.huzhurong.fuck.transaction.support.TempResultSet;

import java.net.SocketAddress;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/1
 */
@Slf4j
public class NettyClient implements Client {

    private NioEventLoopGroup work;
    private ChannelFuture channelFuture;

    public NettyClient(Provider provider, ISerialization serialization) {
        this.provider = provider;
        this.serialization = serialization;
    }

    @Getter
    @Setter
    private Provider provider;

    @Getter
    @Setter
    private ISerialization serialization;

    @Override
    public ChannelFuture connect(String host, Integer port) {
        Bootstrap bootstrap = new Bootstrap();
        try {
            work = new NioEventLoopGroup(1, TempResultSet.defaultThreadFactory());
            bootstrap.group(work)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast(new LoggingHandler())
                                    .addLast(new MessageDecoder(serialization))
                                    .addLast(new MessageEncoder(serialization))
                                    .addLast(new NettyConnectManageHandler())
                                    .addLast(new ClientTransactionHandler(provider));
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true);
            //等到channel激活的时候，ClientTransactionHandler#channelActive已经执行了
            channelFuture = bootstrap.connect(host, port).sync();
            return channelFuture;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 这里是出去的数据信息
     */
    class NettyConnectManageHandler extends ChannelDuplexHandler {
        @Override
        public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress,
                            ChannelPromise promise) throws Exception {
            super.connect(ctx, remoteAddress, localAddress, promise);
            if (log.isDebugEnabled()) {
                log.debug("CONNECT SERVER [{}]", remoteAddress.toString());
            }
        }
    }

    @Override
    public void disConnect() throws InterruptedException {
        channelFuture.channel().closeFuture().sync();
        if (work != null) {
            work.shutdownGracefully();
        }
    }
}
