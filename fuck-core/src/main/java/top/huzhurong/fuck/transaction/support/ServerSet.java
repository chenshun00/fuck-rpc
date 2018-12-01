package top.huzhurong.fuck.transaction.support;

import top.huzhurong.fuck.transaction.Server;

import java.util.HashMap;
import java.util.Map;

/**
 * 根据引用的服务信息从zk上找到provider列表，然后建立tcp长链接
 * <p>
 * 我们是serviceName 去代表channel呢还是怎么去代表channel
 * 首先一个channel代表是一个服务的通道，所有我们需要找出所有的共同的service---host:port
 *
 * @author luobo.cs@raycloud.com
 * @since 2018/11/30
 */
public class ServerSet {
    private static Map<String, Server> serverMap = new HashMap<>();

    public static void put(String path, Server server) {

    }


}
