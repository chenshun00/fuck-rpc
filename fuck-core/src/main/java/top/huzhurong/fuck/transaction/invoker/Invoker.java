package top.huzhurong.fuck.transaction.invoker;

import top.huzhurong.fuck.transaction.support.Request;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/9
 */
public abstract class Invoker {
    protected Request request;

    /**
     * 执行消费端请求
     *
     * @return 消费对象
     */
    public abstract Object invoke();

    public Invoker(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }
}
