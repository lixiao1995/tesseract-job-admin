package admin.core.netty.server.handler;

import admin.core.TesseractJobServiceDelegator;
import admin.core.scheduler.TesseractFutureTask;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tesseract.core.dto.TesseractExecutorResponse;
import tesseract.core.util.CommonUtils;

/**
 * @description:
 * @author: nickle
 * @create: 2019-09-25 16:14
 **/
@ChannelHandler.Sharable
@Slf4j
@AllArgsConstructor
public class TesseractTaskExecutorHandler extends ChannelInboundHandlerAdapter {
    private String socket;
    private Integer executorDetailId;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpResponse fullHttpResponse = (FullHttpResponse) msg;
        byte[] bytes = CommonUtils.byteBufToByteArr(fullHttpResponse.content());
        TesseractExecutorResponse response = (TesseractExecutorResponse) TesseractJobServiceDelegator.serializerService.deserialize(bytes);
        Integer fireJobId = (Integer) response.getBody();
        TesseractFutureTask tesseractFutureTask = TesseractJobServiceDelegator.FUTURE_TASK_MAP.get(fireJobId);
        if (tesseractFutureTask == null) {
            log.error("找不到TesseractFutureTask，不可能出现");
        }
        tesseractFutureTask.setT(response);
        tesseractFutureTask.unlock();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.toString());
    }
}
