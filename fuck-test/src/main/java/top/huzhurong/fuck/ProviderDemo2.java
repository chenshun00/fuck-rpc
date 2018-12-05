package top.huzhurong.fuck;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/5
 */
public class ProviderDemo2 {
    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:abc2.xml");
        Thread.sleep(100000000000L);
    }
}
