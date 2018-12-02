package top.huzhurong.fuck.transaction.support;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/11/30
 */
@Getter
@Setter
@ToString
public class Request implements Serializable {
    private String requestId;
    private String serviceName;
    private Method method;
    private Object[] args;
}
