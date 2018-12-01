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
import top.huzhurong.fuck.register.IRegister;
import top.huzhurong.fuck.serialization.ISerialization;
import top.huzhurong.fuck.transaction.Server;
import top.huzhurong.fuck.transaction.support.Provider;

import java.util.Collections;

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

    @Getter
    @Setter
    private Object object;

    private IRegister register;

    public NettyServer(ISerialization serialization, Object object, IRegister register) {
        this.serialization = serialization;
        this.object = object;
        this.register = register;
    }

    @Override
    public void bind(Integer port) {
        if (port <= 1000) {
            port = 11911;
        }
        boss = new NioEventLoopGroup(1);
        work = new NioEventLoopGroup(1);

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            final HeartBeatHandler heartBeatHandler = new HeartBeatHandler(this.object);
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

            Class<?> aClass = this.object.getClass();

            Provider provider = new Provider();
            provider.setHost("127.0.0.1");
            provider.setVersion("1.0.0");
            provider.setPort(port);
            provider.setServiceName(aClass.getName());
            register.registerService(Collections.singletonList(provider));

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
