package top.huzhurong.fuck.autoconfigure;

import org.springframework.context.EnvironmentAware;

import java.io.Serializable;

/**
 * @author chenshun00@gmail.com
 * @since 2019/5/3
 */
public interface FuckConfigBinder extends EnvironmentAware {
    /**
     * Set whether to ignore unknown fields, that is, whether to ignore bind
     * parameters that do not have corresponding fields in the target object.
     * <p>Default is "true". Turn this off to enforce that all bind parameters
     * must have a matching field in the target object.
     *
     * @see #bind
     */
    void setIgnoreUnknownFields(boolean ignoreUnknownFields);

    /**
     * Set whether to ignore invalid fields, that is, whether to ignore bind
     * parameters that have corresponding fields in the target object which are
     * not accessible (for example because of null values in the nested path).
     * <p>Default is "false".
     *
     * @see #bind
     */
    void setIgnoreInvalidFields(boolean ignoreInvalidFields);

    <C extends Serializable> void bind(String prefix, C bean);
}
