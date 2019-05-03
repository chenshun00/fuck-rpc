package top.huzhurong.fuck.comsumer.view;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import top.huzhurong.fuck.boot.AbcInterface;
import top.huzhurong.fuck.spring.annotation.Reference;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author chenshun00@gmail.com
 * @since 2019/5/3
 */
@RestController
public class AbcConsumerCtrl {

    @Reference(version = "0.0.1", registry = "addressBean")
    private AbcInterface abcInterface;

    @GetMapping("test")
    @ResponseBody
    public Object test() {
        return abcInterface.good();
    }

    @GetMapping("future")
    @ResponseBody
    public Object future() throws InterruptedException {
        CompletableFuture<List<String>> future = abcInterface.future();
        AtomicReference<Object> resut = new AtomicReference<>();
        future.whenComplete((r, e) -> resut.set(r));
        Thread.sleep(100L);
        return resut.get() == null ? "gg" : resut.get();
    }

}
