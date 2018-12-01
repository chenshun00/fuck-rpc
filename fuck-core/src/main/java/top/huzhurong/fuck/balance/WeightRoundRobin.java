package top.huzhurong.fuck.balance;

import top.huzhurong.fuck.transaction.support.Provider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 加权轮询（Weight Round Robin）法
 *
 * @author luobo.cs@raycloud.com
 * @since 2018/11/24
 */
public class WeightRoundRobin implements LoadBalance {

    private Integer curSize = 0;

    private Lock lock = new ReentrantLock();

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
        Provider provider = null;
        try {
            lock.tryLock(100, TimeUnit.SECONDS);
            if (curSize >= providerList.size()) {
                curSize = 0;
            }
            provider = providerList.get(curSize);
        } catch (InterruptedException ignore) {

        } finally {
            curSize++;
            lock.unlock();
        }
        if (provider == null) {
            provider = providers.get(0);
        }
        return provider;
    }

}
