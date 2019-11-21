package tesseract.core.executor.netty.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;
import tesseract.core.dto.TesseractExecutorRequest;
import tesseract.core.dto.TesseractExecutorResponse;
import tesseract.core.executor.ClientServiceDelegator;
import tesseract.core.netty.HandleBean;
import tesseract.core.netty.ICommandHandler;
import tesseract.core.util.CommonUtils;
import tesseract.core.util.HttpUtils;

/**
 * @description: 执行任务处理器
 * @author: nickle
 * @create: 2019-09-09 10:07
 **/
@Slf4j
public class ExecuteTaskCommandHandler implements ICommandHandler {
    @Override
    public void handleCommand(HandleBean handleBean, Channel channel) {
        byte[] bytes = CommonUtils.byteBufToByteArr((ByteBuf) handleBean.getData());
        TesseractExecutorRequest request = (TesseractExecutorRequest) ClientServiceDelegator.serializerService.deserialize(bytes);
        log.info("接收到任务:{}", request);
        TesseractExecutorResponse executorResponse = ClientServiceDelegator.tesseractExecutor.execute(request);
        try {
            FullHttpResponse fullHttpResponse = HttpUtils.buildFullHttpResponse(ClientServiceDelegator.serializerService.serialize(executorResponse), null);
            channel.writeAndFlush(fullHttpResponse).sync();
        } catch (InterruptedException e) {
            log.error("中断异常，不应该出现");
        }
    }
}
