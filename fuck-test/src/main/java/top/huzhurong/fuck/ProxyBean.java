package top.huzhurong.fuck;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.lang.reflect.Proxy;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/2
 */
public class ProxyBean implements FactoryBean, InitializingBean {

    private Class name;
    private Object object;

    @Override
    public Object getObject() {
        return object;
    }

    @Override
    public Class<?> getObjectType() {
        return name;
    }

    @Override
    public void afterPropertiesSet() {
        this.build();
    }

    private void build() {
        Object obj = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{this.name}, (proxy, method, args) -> {
            System.out.println(method.getName() + "\t" + method);
            if (method.getName().equalsIgnoreCase("name")) {
                return "调用name方法";
            }
            if (method.getName().equalsIgnoreCase("toString")) {
                return "调用toString方法";
            }
            return "111";
        });
        Assert.notNull(obj, "代理对象不能为空");
        this.object = obj;
    }

    public Class getName() {
        return name;
    }

    public void setName(Class name) {
        this.name = name;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
