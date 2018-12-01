package top.huzhurong.fuck.serialization;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/11/29
 */
public interface ISerialization {

    <T> byte[] serialize(T obj);


    <T> T deSerialize(byte[] bytes, Class<T> tClass);

}
