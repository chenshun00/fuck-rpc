package top.huzhurong.fuck.serialization.protobuff;

import org.junit.Test;
import top.huzhurong.fuck.serialization.ISerialization;
import top.huzhurong.fuck.serialization.SerializationFactory;
import top.huzhurong.fuck.transaction.support.Request;

import java.lang.reflect.Method;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/2
 */
public class ProtoBuffSerilizeTest {

    @Test
    public void serialize() throws NoSuchMethodException {
        ISerialization resolve = SerializationFactory.resolve("jdk","info");
        Request request = new Request();
        Method serialize1 = ProtoBuffSerilizeTest.class.getDeclaredMethod("serialize");
        request.setMethodName(serialize1.getName());
        request.setParameters(serialize1.getParameterTypes());
        request.setArgs(null);
        request.setServiceName("abc.abc");
        request.setRequestId(UUID.randomUUID().toString());
        byte[] serialize = resolve.serialize(request);
    }


    @Test
    public void testVoidMethod() throws NoSuchMethodException {
        Method testVoidMethod = ProtoBuffSerilizeTest.class.getDeclaredMethod("testVoidMethod");
        Class<?> returnType = testVoidMethod.getReturnType();
        System.out.println(returnType.toString());
    }
}