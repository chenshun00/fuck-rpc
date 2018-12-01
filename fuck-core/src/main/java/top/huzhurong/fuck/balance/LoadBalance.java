package top.huzhurong.fuck.balance;

import top.huzhurong.fuck.transaction.support.Provider;

import java.util.List;
import java.util.Random;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/11/24
 */
public interface LoadBalance {

    Provider getProvider(List<Provider> providers) throws Exception;

    default void check(List<Provider> providers) {
        if (providers == null || providers.size() == 0) {
            throw new IllegalArgumentException("服务端列表不能为空");
        }
    }


    default Integer size(Integer max, Integer min) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

}
