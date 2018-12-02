package top.huzhurong.fuck.transaction.support;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/1
 */
public class ChannelMap {
    private static Map<String, Channel> channelMap = new HashMap<>();

    public static void put(String info, Channel channel) {
        channelMap.put(info, channel);
    }

    public static Channel get(String info) {
        return channelMap.get(info);
    }

}
