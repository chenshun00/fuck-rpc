package top.huzhurong.fuck;

import java.util.concurrent.CompletableFuture;

/**
 * @author chenshun00@gmail.com
 * @since 2019/4/30
 */
public interface AsyncService {
    CompletableFuture<String> asyncName(String name);
}
