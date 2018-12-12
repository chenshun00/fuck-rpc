package top.huzhurong.fuck.filter;

import lombok.extern.slf4j.Slf4j;
import top.huzhurong.fuck.filter.annotation.FuckFilterChain;
import top.huzhurong.fuck.transaction.support.Request;
import top.huzhurong.fuck.transaction.support.Response;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/4
 */
@Slf4j
public class LogFilter implements FuckFilter {
    @Override
    public Object filter(Request request, Response response, FuckFilterChain fuckFilterChain) {
        if (log.isDebugEnabled()) {
            log.debug("rpc请求:{}", request);
        }
        return fuckFilterChain.doNext(request, response);
    }
}
