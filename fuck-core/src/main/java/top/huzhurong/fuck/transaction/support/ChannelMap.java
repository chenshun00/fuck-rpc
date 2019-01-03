package top.huzhurong.fuck.transaction.support;

import io.netty.channel.socket.SocketChannel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/1
 */
public class ChannelMap {
    private static Map<String, SocketChannel> channelMap = new HashMap<>();

    public static void put(String info, SocketChannel socketChannel) {
        channelMap.put(info, socketChannel);
    }

    public static SocketChannel get(String info) {
        return channelMap.get(info);
    }

    public static void remove(String info) {
        channelMap.remove(info);
    }
}
