package top.huzhurong.fuck.transaction.netty;

import top.huzhurong.fuck.transaction.support.Response;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/11/30
 */
public class RequestTask implements Callable<Object> {

    private Response response;

    public RequestTask(Response response) {
        this.response = Objects.requireNonNull(response);
    }

    @Override
    public Object call() {
        if (response.getSuccess()) {
            return response.getObject();
        } else {
            throw new RuntimeException(response.getException());
        }
    }
}
