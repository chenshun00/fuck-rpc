package top.huzhurong.fuck.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/4
 */
public class FuckFilterManager {

    private static List<FuckFilter> fuckFilters = new ArrayList<>();

    static {
        ServiceLoader<FuckFilter> operations = ServiceLoader.load(FuckFilter.class);
        for (FuckFilter operation : operations) {
            fuckFilters.add(operation);
        }
    }

    private FuckFilterManager() {
    }

    public final static FuckFilterManager instance = new FuckFilterManager();

    public List<FuckFilter> getFuckFilters() {
        return fuckFilters;
    }

}
