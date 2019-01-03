package top.huzhurong.fuck.serialization;

/**
 * @author chenshun00@gmail.com
 * @since 2018/11/29
 */
public interface ISerialization {

    <T> byte[] serialize(T obj);


    <T> T deSerialize(byte[] bytes, Class<T> tClass);

}
