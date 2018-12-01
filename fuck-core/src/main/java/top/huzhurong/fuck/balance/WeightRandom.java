package top.huzhurong.fuck.balance;

import top.huzhurong.fuck.transaction.support.Provider;

import java.util.ArrayList;
import java.util.List;

/**
 * 加权随机（Weight Random）法
 *
 * @author luobo.cs@raycloud.com
 * @since 2018/11/24
 */
public class WeightRandom implements LoadBalance {

    @Override
    public Provider getProvider(List<Provider> providers) throws CloneNotSupportedException {
        check(providers);
        List<Provider> providerList = new ArrayList<>(32);
        for (Provider provider : providers) {
            Integer weight = provider.getWeight();
            providerList.add(provider);
            for (Integer i = 0; i < weight; i++) {
                providerList.add(provider.clone());
            }
        }
        Integer size = size(providerList.size() - 1, 0);
        Provider provider = providerList.get(size);
        if (provider == null) {
            provider = providers.get(0);
        }
        return provider;
    }
}
