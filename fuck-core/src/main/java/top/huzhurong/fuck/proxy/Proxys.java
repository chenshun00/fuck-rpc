package top.huzhurong.fuck.proxy;

import top.huzhurong.fuck.transaction.support.Request;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.UUID;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/11/30
 */
public class Proxys implements InvocationHandler {

    private Class<?> aClass;

    public Proxys(Class<?> aClass) {
        this.aClass = Objects.requireNonNull(aClass);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Request request = new Request();
        request.setRequestId(UUID.randomUUID().toString());
        request.setMethod(method);
        request.setArgs(args);
        request.setServiceName(aClass.getName());

        return null;
    }
}
