package top.huzhurong.fuck.transaction.netty;

import io.netty.channel.ChannelHandlerContext;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import top.huzhurong.fuck.filter.FuckFilter;
import top.huzhurong.fuck.filter.FuckFilterManager;
import top.huzhurong.fuck.filter.annotation.FuckFilterChain;
import top.huzhurong.fuck.transaction.invoker.ServerInvoker;
import top.huzhurong.fuck.transaction.support.Request;
import top.huzhurong.fuck.transaction.support.Response;
import top.huzhurong.fuck.util.MethodUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author chenshun00@gmail.com
 * @since 2018/11/30
 */
public class ResponseTask implements Runnable {

    private Request request;
    private ChannelHandlerContext channelHandlerContext;
    private ApplicationContext applicationContext;

    public ResponseTask(Request request, ChannelHandlerContext channelHandlerContext, ApplicationContext applicationContext) {
        this.request = Objects.requireNonNull(request, "request不能为空");
        this.channelHandlerContext = Objects.requireNonNull(channelHandlerContext);
        this.applicationContext = Objects.requireNonNull(applicationContext);
    }

    @Override
    public void run() {
        Assert.notNull(this.request, "request 不能为空");
        List<FuckFilter> fuckFilters = FuckFilterManager.instance.getProviderFilter();
        FuckFilterChain chain = new FuckFilterChain(fuckFilters, new ServerInvoker(this.request, this.applicationContext));
        Response response = new Response();
        response.setRequestId(this.request.getRequestId());
        response.setSuccess(true);
        Object object = chain.doNext(this.request, response);
        response.setObject(object);
        response.setAsync(this.request.getAsync());
        if (object instanceof Throwable) {
            response.setObject(null);
            response.setException((Throwable) object);
        }
        channelHandlerContext.writeAndFlush(response);
    }
}
