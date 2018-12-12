package top.huzhurong.fuck.util;

import java.lang.reflect.Method;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/12
 */
public class MethodUtils {

    public static boolean isVoid(Method method) {
        if (method == null) {
            return false;
        }
        return method.getReturnType().toString().equals("void");
    }

}
