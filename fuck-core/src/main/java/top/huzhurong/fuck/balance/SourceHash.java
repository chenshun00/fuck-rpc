package top.huzhurong.fuck.balance;

import top.huzhurong.fuck.transaction.support.Provider;

import java.util.List;

/**
 * 源地址哈希（Hash）法
 *
 * @author luobo.cs@raycloud.com
 * @since 2018/11/24
 */
public class SourceHash implements LoadBalance {

    @Override
    public Provider getProvider(List<Provider> providers) {
        check(providers);
        return null;
    }

}
