package top.huzhurong.fuck.spring.annotation;

import java.lang.annotation.*;

/**
 * @author chenshun00@gmail.com
 * @since 2019/5/3
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface Reference {

    Class<?> interfaceClass() default void.class;
    String interfaceName() default "";
    String version() default "";

    String registry() default "";

    int timeout() default 10;
}
