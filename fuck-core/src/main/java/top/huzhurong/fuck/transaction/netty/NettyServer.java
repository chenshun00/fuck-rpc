package top.huzhurong.fuck.transaction.netty;

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

import java.util.Objects;

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

    @Override
    public void bind(Integer port) {
        if (port <= 1000) {
            port = 11911;
        }
        boss = new NioEventLoopGroup(1);
        work = new NioEventLoopGroup(1);

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            final HeartBeatHandler heartBeatHandler = new HeartBeatHandler();
            serverBootstrap.group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new MessageDecoder(serialization))
                                    .addLast(new MessageEncoder(serialization))
                                    .addLast(heartBeatHandler);
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unRegister() {
        work.shutdownGracefully();
        boss.shutdownGracefully();
    }
}
