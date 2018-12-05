package top.huzhurong.fuck.filter.annotation;

import top.huzhurong.fuck.filter.FuckFilter;

import java.util.List;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/5
 */
public class FuckFIlterChain {
    /**
     * <p>handle message and invoke next</p>
     *
     * @param message the message will be handle(any param include Object)
     */
    public Object doNext(String message) {
        if (index == filterList.size() - 1) {
            return invoker.toString();
        } else {
            return filterList.get(++index).filter(null, null, this);
        }
    }

    public FuckFIlterChain(List<FuckFilter> filterList, Object invoker) {
        this.filterList = filterList;
        this.invoker = invoker;
    }


    /**
     * index , compare the index and size , invoke handler
     */
    private static Integer index = -1;
    private List<FuckFilter> filterList;
    private Object invoker;

    public Object getInvoker() {
        return invoker;
    }

    public void setInvoker(Object invoker) {
        this.invoker = invoker;
    }

    public List<FuckFilter> getFilterList() {
        return filterList;
    }

    public void setFilterList(List<FuckFilter> filterList) {
        this.filterList = filterList;
    }
}
