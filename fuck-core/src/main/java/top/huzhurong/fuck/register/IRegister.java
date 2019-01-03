package top.huzhurong.fuck.register;

import top.huzhurong.fuck.transaction.support.Consumer;
import top.huzhurong.fuck.transaction.support.Provider;

import java.util.List;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/1
 */
public interface IRegister {

    void registerService(List<Provider> provider);

    List<Provider> discover(String serviceName, String version);

    default void registerConsumer(Consumer consumer) {

    }

    default void subscribe(String service) {

    }
}
