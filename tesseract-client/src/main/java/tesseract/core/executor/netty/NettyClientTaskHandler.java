package tesseract.core.executor.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import tesseract.core.dto.TesseractExecutorRequest;
import tesseract.core.executor.ClientServiceDelegator;
import tesseract.core.util.CommonUtils;

/**
 * 这个类用于netty server 接收任务执行请求操作
 *
 * @author nickle
 */
@Slf4j
public class NettyClientTaskHandler extends ChannelInboundHandlerAdapter {
    /**
     * 接收任务执行请求执行
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        FullHttpRequest httpRequest = (FullHttpRequest) msg;
        ByteBuf content = httpRequest.content();
        byte[] bytes = CommonUtils.byteBufToByteArr(content);
        System.out.println(new String(bytes));
        TesseractExecutorRequest executorResponse = (TesseractExecutorRequest) ClientServiceDelegator.serializerService.deserialize(bytes);
        log.info("接收到任务:{}", executorResponse);
        ClientServiceDelegator.tesseractExecutor.execute(executorResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        log.error("NettyClientCommandDispatcher 发生异常:{}", cause.getMessage());
        ctx.fireExceptionCaught(cause);
    }
}
