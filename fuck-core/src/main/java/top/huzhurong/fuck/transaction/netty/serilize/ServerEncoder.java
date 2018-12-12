package top.huzhurong.fuck.transaction.netty.serilize;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import top.huzhurong.fuck.serialization.ISerialization;
import top.huzhurong.fuck.transaction.support.Response;

/**
 * provider 和 consumer decoder 和 encoder 合并到一起
 *
 * @author luobo.cs@raycloud.com
 * @since 2018/12/2
 */
@Slf4j
@Deprecated
public class ServerEncoder extends MessageToByteEncoder {
    private ISerialization serialization;

    public ServerEncoder(ISerialization serialization) {
        this.serialization = serialization;
    }

    /**
     * 第一步是要区分是request还是response
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Object obj, ByteBuf byteBuf) {
        Response response = (Response) obj;
        byte[] bytes = serialization.serialize(response);
        int dataLength = bytes.length;
        byteBuf.writeInt(dataLength);
        byteBuf.writeBytes(bytes);
        //下边这一行是强制写入并且刷新，如果这么写，在某些版本会抛出引用异常，因为每一次writeAndFlush
        //之后都会减少一次引用，而netty最后会自动帮我们减少一次引用
        if (log.isInfoEnabled()) {
            log.info("服务端返回请求消息:{}", response);
        }
        //可能出现bug，但是又不是很多确定
    }
}
