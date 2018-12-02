package top.huzhurong.fuck;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/1
 */
public class App {
    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("abc.xml");
        UserService userService = (UserService) applicationContext.getBean("userService");
        System.out.println(userService.name());
    }
}
