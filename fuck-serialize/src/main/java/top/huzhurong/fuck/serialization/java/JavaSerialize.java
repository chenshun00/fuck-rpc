package top.huzhurong.fuck.serialization.java;

import top.huzhurong.fuck.serialization.ISerialization;

import java.io.*;

/**
 * @author chenshun00@gmail.com
 * @since 2018/11/29
 */
public class JavaSerialize implements ISerialization {
    @Override
    public <T> byte[] serialize(T obj) {
        if (obj == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.flush();
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to serialize object of type: " + obj.getClass(), ex);
        }
        return baos.toByteArray();
    }

    @Override
    public <T> T deSerialize(byte[] bytes, Class<T> tClass) {
        if (bytes == null) {
            return null;
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return tClass.cast(ois.readObject());
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to deserialize object", ex);
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Failed to deserialize object type", ex);
        }
    }

    @Override
    public String toString() {
        return "jdk";
    }
}
