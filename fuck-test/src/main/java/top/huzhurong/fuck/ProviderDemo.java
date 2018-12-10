package top.huzhurong.fuck;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/1
 */
public class ProviderDemo {
    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:abc.xml");
        DefaultService bean = applicationContext.getBean(DefaultService.class);
        System.out.println(bean);
        UserService bean1 = applicationContext.getBean(UserService.class);
        System.out.println(bean1);
        Thread.sleep(100000000000L);
    }
}
