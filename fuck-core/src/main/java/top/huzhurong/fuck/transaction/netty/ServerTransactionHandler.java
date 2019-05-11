package top.huzhurong.fuck.transaction.netty;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import top.huzhurong.fuck.transaction.support.Request;
import top.huzhurong.fuck.transaction.support.Response;
import top.huzhurong.fuck.transaction.support.TempResultSet;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author chenshun00@gmail.com
 * @since 2018/12/2
 */
@Sharable
@Slf4j
public class ServerTransactionHandler extends SimpleChannelInboundHandler<Serializable> {

    private ExecutorService responseTask = Executors.newFixedThreadPool(10, TempResultSet.defaultThreadFactory());

    private ApplicationContext applicationContext;

    public ServerTransactionHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (log.isInfoEnabled()) {
            log.info("channel active :{}", ctx.channel());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (log.isWarnEnabled()) {
            log.warn("channel inactive :{}", ctx.channel());
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Serializable serializable) {
        if (serializable instanceof Request) {
            Request request = (Request) serializable;
            if (request.getCode() == 2) {
                Response response = new Response();
                response.setRequestId(request.getRequestId());
                response.setAsync(Boolean.TRUE);
                response.setSuccess(Boolean.TRUE);
                response.setObject("PONG");
                channelHandlerContext.writeAndFlush(response);
            } else {
                responseTask.execute(new ResponseTask(request, channelHandlerContext, this.applicationContext));
            }
        }
    }
}
