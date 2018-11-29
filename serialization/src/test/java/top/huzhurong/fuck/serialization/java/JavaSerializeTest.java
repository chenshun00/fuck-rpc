package top.huzhurong.fuck.serialization.java;

import org.junit.Assert;
import org.junit.Test;
import top.huzhurong.fuck.serialization.BaseTest;
import top.huzhurong.fuck.serialization.User;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/11/29
 */
public class JavaSerializeTest extends BaseTest {

    private JavaSerialize javaSerialize = new JavaSerialize();

    @Test
    public void serialize() {
        byte[] serialize = javaSerialize.serialize(user);
        Assert.assertTrue(serialize.length > 0);
        User uu = javaSerialize.deSerialize(serialize, User.class);
        Assert.assertEquals(user, uu);
    }

    @Test
    public void deSerialize() {
    }
}
