package top.huzhurong.fuck;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/2
 */
public class AppTest {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("test.xml");
        UserService bean = applicationContext.getBean(UserService.class);
        String name = bean.name();
        System.out.println(name);
    }
}
