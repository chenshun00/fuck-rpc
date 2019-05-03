package top.huzhurong.fuck.boot;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author chenshun00@gmail.com
 * @since 2019/5/3
 */
public interface AbcInterface {
    String good();

    CompletableFuture<List<String>> future();
}
