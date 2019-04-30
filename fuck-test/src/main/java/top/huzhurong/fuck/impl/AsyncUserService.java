package top.huzhurong.fuck.impl;

import top.huzhurong.fuck.AsyncService;

import java.util.concurrent.CompletableFuture;

/**
 * @author chenshun00@gmail.com
 * @since 2019/4/30
 */
public class AsyncUserService implements AsyncService {
    @Override
    public CompletableFuture<String> asyncName(String name) {
        return null;
    }
}
