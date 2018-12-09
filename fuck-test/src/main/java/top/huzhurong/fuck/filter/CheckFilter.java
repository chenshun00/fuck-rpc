package top.huzhurong.fuck.filter;

import top.huzhurong.fuck.filter.annotation.Consumer;
import top.huzhurong.fuck.filter.annotation.FuckFilterChain;
import top.huzhurong.fuck.transaction.support.Request;
import top.huzhurong.fuck.transaction.support.Response;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/9
 */
@Consumer
public class CheckFilter implements FuckFilter {
    @Override
    public Object filter(Request request, Response response, FuckFilterChain fuckFilterChain) {
        System.out.println("check");
        return fuckFilterChain.doNext(request, response);
    }
}
