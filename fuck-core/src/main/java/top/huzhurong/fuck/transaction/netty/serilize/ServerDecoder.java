package top.huzhurong.fuck.transaction.netty.serilize;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import top.huzhurong.fuck.serialization.ISerialization;
import top.huzhurong.fuck.transaction.support.Request;

import java.util.List;

/**
 * provider 和 consumer decoder 和 encoder 合并到一起
 *
 * @author luobo.cs@raycloud.com
 * @since 2018/12/2
 */
@Slf4j
@Deprecated
public class ServerDecoder extends ByteToMessageDecoder {
    private static final int HEAD_LENGTH = 4;//最小数据包头长度

    private ISerialization serialization;

    public ServerDecoder(ISerialization serialization) {
        this.serialization = serialization;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) {

        if (byteBuf.readableBytes() <= HEAD_LENGTH) {
            return;
        }
        byteBuf.markReaderIndex();//标记位置

        int dataLength = byteBuf.readInt();

        if (byteBuf.readableBytes() < dataLength) {
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] dataArray = new byte[dataLength];
        byteBuf.readBytes(dataArray);
        Request request = serialization.deSerialize(dataArray, Request.class);
        if (log.isDebugEnabled()) {
            log.debug("接受到消费者请求:{},请求内容:{}", ctx.channel().toString(), request);
        }
        list.add(request);
    }
}
