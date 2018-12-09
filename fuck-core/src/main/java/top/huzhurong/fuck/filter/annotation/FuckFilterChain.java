package top.huzhurong.fuck.filter.annotation;

import top.huzhurong.fuck.filter.FuckFilter;
import top.huzhurong.fuck.transaction.support.Invoker;
import top.huzhurong.fuck.transaction.support.Request;
import top.huzhurong.fuck.transaction.support.Response;

import java.util.List;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/5
 */
public class FuckFilterChain {

    public Object doNext(Request request, Response response) {
        if (index >= filterList.size() - 1) {
            return invoker.invoke();
        }
        return filterList.get(++index).filter(request, response, this);
    }

    public FuckFilterChain(List<FuckFilter> filterList, Invoker invoker) {
        this.filterList = filterList;
        this.invoker = invoker;
    }

    /**
     * index , compare the index and size , invoke handler
     */
    private Integer index = -1;
    private List<FuckFilter> filterList;
    private Invoker invoker;
}
