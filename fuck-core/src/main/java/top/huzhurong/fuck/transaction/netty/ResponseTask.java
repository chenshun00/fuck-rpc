package top.huzhurong.fuck.transaction.netty;

import io.netty.channel.ChannelHandlerContext;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;
import top.huzhurong.fuck.transaction.support.Request;
import top.huzhurong.fuck.transaction.support.Response;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/11/30
 */
public class ResponseTask implements Runnable {

    private Request request;
    private ChannelHandlerContext channelHandlerContext;
    private ApplicationContext applicationContext;

    public ResponseTask(Request request, ChannelHandlerContext channelHandlerContext, ApplicationContext applicationContext) {
        this.request = Objects.requireNonNull(request, "request不能为空");
        this.channelHandlerContext = Objects.requireNonNull(channelHandlerContext);
        this.applicationContext = Objects.requireNonNull(applicationContext);
    }

    @Override
    public void run() {
        String serviceName = request.getServiceName();
        String methodName = request.getMethodName();
        Class<?>[] parameters = request.getParameters();
        Object[] args = request.getArgs();
        Response response = new Response();
        response.setRequestId(request.getRequestId());
        response.setSuccess(false);
        try {
            Class<?> aClass = ClassUtils.forName(serviceName, null);
            Object obj = applicationContext.getBean(aClass);
            Method method = obj.getClass().getDeclaredMethod(methodName, parameters);
            Object invoke = method.invoke(obj, args);
            System.out.println("invoke:" + invoke);
            response.setSuccess(true);
            response.setObject(invoke);
        } catch (ClassNotFoundException | IllegalAccessException e) {
            response.setException(e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            response.setException(e);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            response.setException(e.getTargetException());
        }
        channelHandlerContext.writeAndFlush(response);
    }
}
