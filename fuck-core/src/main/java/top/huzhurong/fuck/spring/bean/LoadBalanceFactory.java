package top.huzhurong.fuck.spring.bean;

import org.springframework.util.StringUtils;
import top.huzhurong.fuck.balance.*;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/2
 */
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
                throw new RuntimeException("未知的均衡方案");
        }
        return finalBalance;
    }
}
