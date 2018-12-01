package top.huzhurong.fuck.proxy;

import lombok.Getter;
import lombok.Setter;
import top.huzhurong.fuck.balance.LoadBalance;
import top.huzhurong.fuck.balance.RoundRobin;
import top.huzhurong.fuck.register.IRegister;
import top.huzhurong.fuck.register.zk.ZkRegister;
import top.huzhurong.fuck.transaction.support.Provider;
import top.huzhurong.fuck.transaction.support.Request;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/11/30
 */
public class Proxys implements InvocationHandler {

    @Getter
    private Class<?> aClass;
    @Getter
    @Setter
    private LoadBalance loadBalance;

    public Proxys(Class<?> aClass, String version, IRegister register, LoadBalance loadBalance) {
        Objects.requireNonNull(register);
        Objects.requireNonNull(aClass);

        this.aClass = aClass;
        this.loadBalance = loadBalance;
        if (this.loadBalance == null) {
            this.loadBalance = new RoundRobin();
        }
        //找到提供者列表
        String name = aClass.getName();
        List<Provider> discover = register.discover(name, version);
        ProviderSet.put(name, discover);
        String path = ZkRegister.root_path + "/" + name + ZkRegister.root_provider;
        ((ZkRegister) register).getZkClient().subscribeChildChanges(path, (parent, children) -> {
            List<Provider> collect = children.stream().map(((ZkRegister) register)::toProvider).collect(Collectors.toList());
            ProviderSet.put(name, collect);
        });
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Request request = new Request();
        request.setRequestId(UUID.randomUUID().toString());
        request.setMethod(method);
        request.setArgs(args);
        request.setServiceName(aClass.getName());

        List<Provider> providerList = ProviderSet.getAll(aClass.getName());
        if (providerList == null || providerList.size() == 0) {
            throw new RuntimeException("provider 列表为空");
        }
        Provider provider = this.loadBalance.getProvider(providerList);

        return null;
    }
}
