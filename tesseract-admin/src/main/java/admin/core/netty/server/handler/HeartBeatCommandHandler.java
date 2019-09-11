package admin.core.netty.server.handler;

import admin.core.netty.server.TesseractJobServiceDelegator;
import admin.service.ITesseractExecutorDetailService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpResponse;
import tesseract.core.dto.TesseractExecutorResponse;
import tesseract.core.dto.TesseractHeartbeatRequest;
import tesseract.core.netty.HandleBean;
import tesseract.core.netty.ICommandHandler;
import tesseract.core.serializer.ISerializerService;
import tesseract.core.util.CommonUtils;
import tesseract.core.util.HttpUtils;

import static tesseract.core.constant.CommonConstant.HEARTBEAT_MAPPING;

/**
 * @description: 心跳处理器
 * @author: nickle
 * @create: 2019-09-09 10:07
 **/
public class HeartBeatCommandHandler implements ICommandHandler {
    @Override
    public void handleCommand(HandleBean handleBean, Channel channel) {
        ISerializerService serializerService = TesseractJobServiceDelegator.getSerializerService();
        ITesseractExecutorDetailService tesseractExecutorDetailService = TesseractJobServiceDelegator.getTesseractExecutorDetailService();
        TesseractHeartbeatRequest heartBeatRequest =
                (TesseractHeartbeatRequest) serializerService.deserialize(CommonUtils.byteBufToByteArr((ByteBuf) handleBean.getData()));
        heartBeatRequest.setSocket(CommonUtils.buildSocket(channel));
        TesseractExecutorResponse response;
        try {
            tesseractExecutorDetailService.heartBeat(heartBeatRequest);
            response = TesseractExecutorResponse.SUCCESS;
        } catch (Exception e) {
            response = TesseractExecutorResponse.FAIL;
        }
        response.setHandlerPath(HEARTBEAT_MAPPING);
        byte[] serialize = serializerService.serialize(response);
        FullHttpResponse fullHttpResponse = HttpUtils.buildFullHttpResponse(serialize, null);
        channel.writeAndFlush(fullHttpResponse);
    }
}
