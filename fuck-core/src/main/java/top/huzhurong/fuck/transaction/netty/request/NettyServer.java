package top.huzhurong.fuck.transaction.netty.request;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import top.huzhurong.fuck.serialization.ISerialization;
import top.huzhurong.fuck.transaction.Server;
import top.huzhurong.fuck.transaction.netty.ServerTransactionHandler;
import top.huzhurong.fuck.transaction.netty.serilize.MessageDecoder;
import top.huzhurong.fuck.transaction.netty.serilize.MessageEncoder;

/**
 * netty 服务端
 *
 * @author chenshun00@gmail.com
 * @since 2018/11/30
 */
public class NettyServer implements Server {

    private NioEventLoopGroup boss;
    private NioEventLoopGroup work;

    @Getter
    @Setter
    private ISerialization serialization;

    @Getter
    @Setter
    private ApplicationContext applicationContext;

    public NettyServer(ISerialization serialization, ApplicationContext applicationContext) {
        this.serialization = serialization;
        this.applicationContext = applicationContext;
    }

    private ChannelFuture channelFuture;

    @Override
    public void bind(Integer port) {
        boss = new NioEventLoopGroup(1);
        work = new NioEventLoopGroup(1);

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            final ServerTransactionHandler serverTransactionHandler = new ServerTransactionHandler(this.applicationContext);
            serverBootstrap.group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast(new LoggingHandler())
                                    .addLast(new MessageDecoder(serialization))
                                    .addLast(new MessageEncoder(serialization))
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
        if (boss != null) {
            boss.shutdownGracefully();
        }
        if (work != null) {
            work.shutdownGracefully();
        }
    }
}
