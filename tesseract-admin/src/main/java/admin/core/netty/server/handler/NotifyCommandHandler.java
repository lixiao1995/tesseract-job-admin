package admin.core.netty.server.handler;

import admin.core.netty.server.TesseractJobServiceDelegator;
import admin.service.ITesseractLogService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import tesseract.core.dto.TesseractAdminJobNotify;
import tesseract.core.netty.HandleBean;
import tesseract.core.netty.ICommandHandler;
import tesseract.core.serializer.ISerializerService;
import tesseract.core.util.CommonUtils;

/**
 * @description: 任务执行结果通知处理器
 * @author: nickle
 * @create: 2019-09-09 10:06
 **/
public class NotifyCommandHandler implements ICommandHandler {
    @Override
    public void handleCommand(HandleBean handleBean, Channel channel) {
        ISerializerService serializerService = TesseractJobServiceDelegator.serializerService;
        TesseractAdminJobNotify tesseractAdminJobNotify =
                (TesseractAdminJobNotify) serializerService.deserialize(CommonUtils.byteBufToByteArr((ByteBuf) handleBean.getData()));
        //客户端日志回调
        ITesseractLogService tesseractLogService = TesseractJobServiceDelegator.logService;
        tesseractLogService.notify(tesseractAdminJobNotify);
    }
}
