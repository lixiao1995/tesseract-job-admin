package tesseract.core.executor.netty.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import tesseract.core.dto.TesseractStopTaskRequest;
import tesseract.core.executor.ClientServiceDelegator;
import tesseract.core.netty.HandleBean;
import tesseract.core.netty.ICommandHandler;
import tesseract.core.util.CommonUtils;

/**
 * @description: 任务执行结果通知处理器
 * @author: nickle
 * @create: 2019-09-09 10:06
 **/
public class StopTaskCommandHandler implements ICommandHandler {
    @Override
    public void handleCommand(HandleBean handleBean, Channel channel) {
        byte[] bytes = CommonUtils.byteBufToByteArr((ByteBuf) handleBean.getData());
        TesseractStopTaskRequest tesseractStopTaskRequest = (TesseractStopTaskRequest) ClientServiceDelegator.serializerService.deserialize(bytes);

    }
}
