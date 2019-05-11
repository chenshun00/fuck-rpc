package top.huzhurong.fuck.future;

import java.util.concurrent.CompletableFuture;

/**
 * @author chenshun00@gmail.com
 * @since 2019/5/11
 */
public class TestComplateFuture {
    public static void main(String[] args) throws InterruptedException {
        final CompletableFuture<String> future = new CompletableFuture<>();

        future.whenComplete((r, e) -> System.out.println(r + "\t" + e));

        Thread.sleep(5_000);
        System.out.println("====start====");
        new Thread(() -> {
            try {
                Thread.sleep(3_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            future.complete("123");
        }).start();
        Thread.sleep(55_000);
    }
}
