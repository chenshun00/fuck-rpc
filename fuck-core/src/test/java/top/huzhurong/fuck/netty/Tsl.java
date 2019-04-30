package top.huzhurong.fuck.netty;

import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.junit.Test;

/**
 * @author chenshun00@gmail.com
 * @since 2019/4/18
 */
public class Tsl {
    @Test
    public void tslTest() {
        SslContextBuilder sslContextBuilder = SslContextBuilder.forClient().sslProvider(SslProvider.JDK);
        sslContextBuilder.trustManager(InsecureTrustManagerFactory.INSTANCE);

//        sslContextBuilder.keyManager(
//                !isNullOrEmpty(tlsClientCertPath) ? new FileInputStream(tlsClientCertPath) : null,
//                !isNullOrEmpty(tlsClientKeyPath) ? decryptionStrategy.decryptPrivateKey(tlsClientKeyPath, true) : null,
//                !isNullOrEmpty(tlsClientKeyPassword) ? tlsClientKeyPassword : null)
//                .build();


        boolean available = OpenSsl.isAvailable();
        System.out.println(available);
    }
}
