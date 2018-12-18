package top.huzhurong.fuck.serialization;

import lombok.extern.slf4j.Slf4j;
import top.huzhurong.fuck.serialization.java.JavaSerialize;
import top.huzhurong.fuck.serialization.protobuff.ProtoSerialize;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/2
 */
@Slf4j
public abstract class SerializationFactory {
    private SerializationFactory() {
        throw new RuntimeException("实例化异常");
    }

    public static ISerialization resolve(String name, String service) {
        if (name == null || name.equalsIgnoreCase("")) {
            name = "jdk";
        }
        log.info("服务:{} 将使用:{}作为序列方式", service, name);
        ISerialization serialization;

        switch (name) {
            case "proto":
                serialization = new ProtoSerialize();
                break;
            case "jdk":
                serialization = new JavaSerialize();
                break;
            default:
                log.warn("未知的序列化方式:{}，使用jdk作为默认序列化方式", name);
                serialization = new JavaSerialize();
        }
        return serialization;
    }


}
