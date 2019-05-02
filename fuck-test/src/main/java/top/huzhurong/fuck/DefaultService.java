package top.huzhurong.fuck;

import java.util.concurrent.CompletableFuture;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/1
 */
public class DefaultService implements UserService {

    @Override
    public String name() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "111:+chen";
    }

    @Override
    public void noArg() {
        System.out.println("return void");
    }

    @Override
    public CompletableFuture<String> good(String name) {
        return CompletableFuture.supplyAsync(() -> name);
    }
}
