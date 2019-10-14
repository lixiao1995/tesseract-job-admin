package tesseract.core.executor.netty.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import tesseract.core.dto.TesseractExecutorRequest;
import tesseract.core.executor.ClientServiceDelegator;
import tesseract.core.netty.HandleBean;
import tesseract.core.netty.ICommandHandler;
import tesseract.core.util.CommonUtils;

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
        ClientServiceDelegator.tesseractExecutor.execute(request);
    }
}
