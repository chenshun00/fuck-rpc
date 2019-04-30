package top.huzhurong.fuck.transaction;

import io.netty.channel.ChannelFuture;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/1
 */
public interface Client {

    ChannelFuture connect(String host, Integer port);

    void disConnect() throws InterruptedException;
}
