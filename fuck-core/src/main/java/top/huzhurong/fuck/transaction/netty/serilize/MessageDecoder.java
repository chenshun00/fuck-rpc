package top.huzhurong.fuck.transaction.netty.serilize;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import top.huzhurong.fuck.serialization.ISerialization;
import top.huzhurong.fuck.transaction.support.Response;

import java.util.List;

/**
 * @author luobo.cs@raycloud.com
 * @since 2018/11/30
 */
@Slf4j
public class MessageDecoder extends ByteToMessageDecoder {
    private static final int HEAD_LENGTH = 4;//最小数据包头长度

    private ISerialization serialization;

    public MessageDecoder(ISerialization serialization) {
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
        Response response = serialization.deSerialize(dataArray, Response.class);
        if (log.isDebugEnabled()) {
            log.debug("客户端执行rpc请求结果返回:{}", response);
        }
        list.add(response);
    }
}
