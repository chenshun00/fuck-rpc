package top.huzhurong.fuck.serialization.protobuff;

import org.junit.Assert;
import org.junit.Test;
import top.huzhurong.fuck.serialization.BaseTest;
import top.huzhurong.fuck.serialization.ISerialization;
import top.huzhurong.fuck.serialization.User;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/11/29
 */
public class ProtoBuffSerilizeTest extends BaseTest {

    private ISerialization serialization = new ProtoBuffSerilize();

    @Test
    public void serialize() {
        byte[] serialize = serialization.serialize(user);
        Assert.assertTrue(serialize.length > 0);
        User uu = serialization.deSerialize(serialize, User.class);
        Assert.assertEquals(user, uu);
    }

    @Test
    public void deSerialize() {
    }
}
