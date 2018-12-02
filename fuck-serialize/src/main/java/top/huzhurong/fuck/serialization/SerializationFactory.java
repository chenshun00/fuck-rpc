package top.huzhurong.fuck.serialization;

import top.huzhurong.fuck.serialization.java.JavaSerialize;
import top.huzhurong.fuck.serialization.protobuff.ProtoBuffSerilize;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/2
 */
public abstract class SerializationFactory {
    private SerializationFactory() {
        throw new RuntimeException("实例化异常");
    }

    public static ISerialization resolve(String name) {
        if (name == null || name.equalsIgnoreCase("")) {
            name = "protobuff";
        }
        ISerialization serialization;

        switch (name) {
            case "protobuff":
                serialization = new ProtoBuffSerilize();
                break;
            case "jdk":
                serialization = new JavaSerialize();
                break;
            default:
                throw new RuntimeException("不合理的序列化方式");
        }
        return serialization;
    }


}
