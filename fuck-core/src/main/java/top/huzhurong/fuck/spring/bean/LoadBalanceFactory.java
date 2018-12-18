package top.huzhurong.fuck.spring.bean;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import top.huzhurong.fuck.balance.*;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/2
 */
@Slf4j
public abstract class LoadBalanceFactory {

    private LoadBalanceFactory() {
    }

    public static LoadBalance resolve(String loadBalance) {
        if (StringUtils.isEmpty(loadBalance)) {
            loadBalance = "RoundRobin";
        }
        LoadBalance finalBalance;
        switch (loadBalance) {
            case "RandomAl":
                finalBalance = new RandomAl();
                break;
            case "RoundRobin":
                finalBalance = new RoundRobin();
                break;
            case "SourceHash":
                finalBalance = new SourceHash();
                break;
            case "WeightRandom":
                finalBalance = new WeightRandom();
                break;
            case "":
                finalBalance = new WeightRoundRobin();
                break;
            default:
                log.warn("未知的负载算法:{}，使用轮训进行负载", loadBalance);
                finalBalance = new RoundRobin();
        }
        return finalBalance;
    }
}
