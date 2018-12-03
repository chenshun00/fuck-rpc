package top.huzhurong.fuck.transaction.netty.request;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.Setter;
import top.huzhurong.fuck.serialization.ISerialization;
import top.huzhurong.fuck.transaction.Server;
import top.huzhurong.fuck.transaction.netty.ClientTransactionHandler;
import top.huzhurong.fuck.transaction.netty.ServerTransactionHandler;
import top.huzhurong.fuck.transaction.netty.serilize.MessageDecoder;
import top.huzhurong.fuck.transaction.netty.serilize.MessageEncoder;
import top.huzhurong.fuck.transaction.netty.serilize.ServerDecoder;
import top.huzhurong.fuck.transaction.netty.serilize.ServerEncoder;

/**
 * netty 服务端
 *
 * @author luobo.cs@raycloud.com
 * @since 2018/11/30
 */
public class NettyServer implements Server {

    private NioEventLoopGroup boss;
    private NioEventLoopGroup work;

    @Getter
    @Setter
    private ISerialization serialization;

    public NettyServer(ISerialization serialization) {
        this.serialization = serialization;
    }

    private ChannelFuture channelFuture;

    @Override
    public void bind(Integer port) {
        boss = new NioEventLoopGroup(1);
        work = new NioEventLoopGroup(1);

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            final ServerTransactionHandler serverTransactionHandler = new ServerTransactionHandler();
            serverBootstrap.group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast(new ServerDecoder(serialization))
                                    .addLast(new ServerEncoder(serialization))
                                    .addLast(serverTransactionHandler);
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            channelFuture = serverBootstrap.bind(port).sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unRegister() throws InterruptedException {
        channelFuture.channel().closeFuture().sync();
    }
}
