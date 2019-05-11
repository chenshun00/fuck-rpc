package top.huzhurong.fuck;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/2
 */
public class ConsumerDemo {
    static AtomicInteger atomicInteger = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("test.xml");
        UserService bean = applicationContext.getBean(UserService.class);
//        for (int i = 0; i < 100000; i++) {
        String name = bean.name();
        System.out.println(name);
//            atomicInteger.getAndIncrement();
//        }
        TimeUnit.SECONDS.sleep(100000000);
//        System.out.println("atomicInteger:"+atomicInteger);
//        CompletableFuture<String> chen = bean.good("chen");
//        chen.whenComplete((result, e) -> {
//            if (e != null) {
//                System.out.println("抛出了异常:" + e.getMessage());
//            }
//            System.out.println("异步处理结果:" + result);
//        });
        //异步处理，结果还没有返回，主线程就结束了，导致所有的守护线程gg，程序结束
    }
}
