package top.huzhurong.fuck.balance;

import top.huzhurong.fuck.transaction.support.Provider;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 轮询（Round Robin）法
 *
 * @author chenshun00@gmail.com
 * @since 2018/11/24
 */
public class RoundRobin implements LoadBalance {

    private Integer curSize = 0;

    private Lock lock = new ReentrantLock();

    @Override
    public Provider getProvider(List<Provider> providers) {
        check(providers);
        Provider provider = null;
        try {
            lock.tryLock(1, TimeUnit.SECONDS);
            if (curSize >= providers.size()) {
                curSize = 0;
            }
            provider = providers.get(curSize);
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
