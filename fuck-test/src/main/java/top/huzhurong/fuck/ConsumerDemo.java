package top.huzhurong.fuck;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/2
 */
public class ConsumerDemo {
    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("test.xml");
        UserService bean = applicationContext.getBean(UserService.class);
        System.out.println(bean.name());
        System.out.println(bean.name());
        System.out.println(bean.name());
        System.out.println(bean.name());
        System.out.println(bean.name());
        System.out.println(bean.name());
        System.out.println(bean.name());
        System.out.println(bean.name());
        System.out.println(bean.name());
        System.out.println(bean.name());
        System.out.println(bean.name());
        System.out.println(bean.name());


        Thread.sleep(100000000000L);
    }
}
