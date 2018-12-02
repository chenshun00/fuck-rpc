package top.huzhurong.fuck.transaction.netty.serilize;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import top.huzhurong.fuck.serialization.ISerialization;
import top.huzhurong.fuck.serialization.protobuff.ProtoBuffSerilize;
import top.huzhurong.fuck.transaction.support.Request;
import top.huzhurong.fuck.transaction.support.Response;

/**
 * client
 *
 * @author luobo.cs@raycloud.com
 * @since 2018/11/30
 */
public class MessageEncoder extends MessageToByteEncoder {

    private ISerialization serialization;

    public MessageEncoder(ISerialization serialization) {
        this.serialization = serialization;
    }

    /**
     * 第一步是要区分是request还是response
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Object obj, ByteBuf byteBuf) {

        Request request = (Request) obj;
        byte[] bytes = serialization.serialize(request);

        int dataLength = bytes.length;
        byteBuf.writeInt(dataLength);
        byteBuf.writeBytes(bytes);
        //下边这一行是强制写入并且刷新，如果这么写，在某些版本会抛出引用异常，因为每一次writeAndFlush
        //之后都会减少一次引用，而netty最后会自动帮我们减少一次引用

        ctx.writeAndFlush(byteBuf);
    }
}