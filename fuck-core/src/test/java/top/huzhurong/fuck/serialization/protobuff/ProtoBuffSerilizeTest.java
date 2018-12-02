package top.huzhurong.fuck.serialization.protobuff;

import org.junit.Test;
import top.huzhurong.fuck.serialization.ISerialization;
import top.huzhurong.fuck.serialization.SerializationFactory;
import top.huzhurong.fuck.transaction.support.Request;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/12/2
 */
public class ProtoBuffSerilizeTest {

    @Test
    public void serialize() throws NoSuchMethodException {
        ISerialization resolve = SerializationFactory.resolve("jdk");
        Request request = new Request();
        request.setMethod(ProtoBuffSerilizeTest.class.getDeclaredMethod("serialize"));
        request.setArgs(null);
        request.setServiceName("abc.abc");
        request.setRequestId(UUID.randomUUID().toString());
        byte[] serialize = resolve.serialize(request);
    }
}