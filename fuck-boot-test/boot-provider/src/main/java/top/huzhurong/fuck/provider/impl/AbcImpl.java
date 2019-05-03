package top.huzhurong.fuck.provider.impl;

import top.huzhurong.fuck.boot.AbcInterface;
import top.huzhurong.fuck.spring.annotation.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author chenshun00@gmail.com
 * @since 2019/5/3
 */
@Service(version = "0.0.1", registry = "addressBean")
public class AbcImpl implements AbcInterface {

    @Override
    public String good() {
        return "111";
    }

    @Override
    public CompletableFuture<List<String>> future() {
        return CompletableFuture.supplyAsync(() -> Arrays.asList("111", "222"));
    }
}
