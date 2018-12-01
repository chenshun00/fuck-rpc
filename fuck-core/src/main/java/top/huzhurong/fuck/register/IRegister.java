package top.huzhurong.fuck.register;

import top.huzhurong.fuck.transaction.support.Provider;

import java.util.List;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/1
 */
public interface IRegister {

    void registerService(List<Provider> provider);

    List<Provider> discover(String serviceName, String version);

}
