package top.huzhurong.fuck.transaction.invoker;

import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import top.huzhurong.fuck.transaction.netty.cache.ServiceCache;
import top.huzhurong.fuck.transaction.support.Request;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/9
 */
public class ServerInvoker extends Invoker {

    private ApplicationContext applicationContext;

    public Object invoke() {
        Assert.notNull(request, "request不能为空");
        String serviceName = request.getServiceName();
        String methodName = request.getMethodName();
        Class<?>[] parameters = request.getParameters();
        Object[] args = request.getArgs();
        try {
            Object service = ServiceCache.getService(serviceName);
            if (service == null) {
                Class<?> aClass = ClassUtils.forName(serviceName, ClassUtils.getDefaultClassLoader());
                service = applicationContext.getBean(aClass);
                ServiceCache.put(serviceName, service);
            }
            Method method = service.getClass().getDeclaredMethod(methodName, parameters);
            return method.invoke(service, args);
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException e) {
            return e;
        } catch (InvocationTargetException e) {
            return e.getTargetException();
        }
    }

    public ServerInvoker(Request request, ApplicationContext applicationContext) {
        super(request);
        this.applicationContext = applicationContext;
    }
}
