package top.huzhurong.fuck.transaction.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.Setter;
import top.huzhurong.fuck.serialization.ISerialization;
import top.huzhurong.fuck.transaction.Client;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/1
 */
public class NettyClient implements Client {

    private NioEventLoopGroup work;

    @Getter
    @Setter
    private ISerialization serialization;

    @Override
    public void connect(String host, Integer port) {
        Bootstrap bootstrap = new Bootstrap();
        try {
            work = new NioEventLoopGroup();
            bootstrap.group(work)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast(new MessageDecoder(serialization))
                                    .addLast(new MessageEncoder(serialization))
                                    .addLast(new HeartBeatHandler(null));
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture sync = bootstrap.connect(host, port).sync();
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disConnect() {
        if (work != null) {
            work.shutdownGracefully();
        }
    }
}
