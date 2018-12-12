package top.huzhurong.fuck.filter;

import lombok.extern.slf4j.Slf4j;
import top.huzhurong.fuck.filter.annotation.Consumer;
import top.huzhurong.fuck.filter.annotation.FuckFilterChain;
import top.huzhurong.fuck.transaction.support.Request;
import top.huzhurong.fuck.transaction.support.Response;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/9
 */
@Consumer
@Slf4j
public class CheckFilter implements FuckFilter {
    @Override
    public Object filter(Request request, Response response, FuckFilterChain fuckFilterChain) {
        log.info("消费端过滤器测试:{}", request);
        return fuckFilterChain.doNext(request, response);
    }
}
