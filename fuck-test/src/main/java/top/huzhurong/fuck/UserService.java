package top.huzhurong.fuck;

import java.util.concurrent.CompletableFuture;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/1
 */
public interface UserService {
    String name();

    void noArg();
    //CompletableFuture为异步返回结果
    CompletableFuture<String> good(String name);
}
