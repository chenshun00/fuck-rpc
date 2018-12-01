package top.huzhurong.fuck.transaction;

import org.junit.Test;
import top.huzhurong.fuck.util.NetUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/1
 */
public class IpUtilTest {

    @Test
    public void getIp() throws UnknownHostException {
        InetAddress localHost = InetAddress.getLocalHost();
        System.out.println(localHost);
        String hostAddress = localHost.getHostAddress();
        System.out.println(hostAddress);
    }


    @Test
    public void netUtil() {
        String localHost = NetUtils.getLocalHost();
        System.out.println(localHost);
    }

}