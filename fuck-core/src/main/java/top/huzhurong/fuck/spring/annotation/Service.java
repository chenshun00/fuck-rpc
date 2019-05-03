package top.huzhurong.fuck.spring.annotation;

import java.lang.annotation.*;

/**
 * @author chenshun00@gmail.com
 * @since 2019/5/2
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {
    Class<?> interfaceClass() default void.class;

    String interfaceName() default "";

    String version() default "";

    int weight() default 0;
    
    String registry();

    String serialization() default "jdk";
}
