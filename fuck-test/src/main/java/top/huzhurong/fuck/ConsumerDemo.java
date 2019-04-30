package top.huzhurong.fuck;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/2
 */
public class ConsumerDemo {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("test.xml");
        UserService bean = applicationContext.getBean(UserService.class);
        System.out.println(bean.name());
    }
}
