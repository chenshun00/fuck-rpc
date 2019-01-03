package top.huzhurong.fuck.filter;

import org.springframework.lang.Nullable;
import top.huzhurong.fuck.filter.annotation.FuckFilterChain;
import top.huzhurong.fuck.transaction.support.Request;
import top.huzhurong.fuck.transaction.support.Response;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/4
 */
public interface FuckFilter {

    /**
     * 服务端和消费端过滤器，如果是消费端response为空
     *
     * @param request         执行端请求
     * @param response        服务端response，消费端为null
     * @param fuckFilterChain 过滤链
     */
    Object filter(Request request, @Nullable Response response, FuckFilterChain fuckFilterChain);

}
