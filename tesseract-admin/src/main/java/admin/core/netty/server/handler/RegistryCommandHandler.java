package admin.core.netty.server.handler;

import admin.core.TesseractJobServiceDelegator;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpResponse;
import tesseract.core.constant.CommonConstant;
import tesseract.core.dto.TesseractAdminRegistryRequest;
import tesseract.core.dto.TesseractAdminRegistryResDTO;
import tesseract.core.dto.TesseractExecutorResponse;
import tesseract.core.netty.HandleBean;
import tesseract.core.netty.ICommandHandler;
import tesseract.core.serializer.ISerializerService;
import tesseract.core.util.CommonUtils;
import tesseract.core.util.HttpUtils;

import java.net.InetSocketAddress;

/**
 * @description: 注册处理器
 * @author: nickle
 * @create: 2019-09-09 10:05
 **/
public class RegistryCommandHandler implements ICommandHandler {
    @Override
    public void handleCommand(HandleBean handleBean, Channel channel) throws Exception {
        ISerializerService serializerService = TesseractJobServiceDelegator.serializerService;
        TesseractAdminRegistryRequest tesseractAdminRegistryRequest =
                (TesseractAdminRegistryRequest) serializerService.deserialize(CommonUtils.byteBufToByteArr((ByteBuf) handleBean.getData()));
        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        tesseractAdminRegistryRequest.setIp(socketAddress.getHostName());
        tesseractAdminRegistryRequest.setPort(tesseractAdminRegistryRequest.getPort());
        TesseractAdminRegistryResDTO registry = TesseractJobServiceDelegator.executorService.registry(tesseractAdminRegistryRequest);
        TesseractExecutorResponse success = new TesseractExecutorResponse(TesseractExecutorResponse.SUCCESS_STATUS, registry, CommonConstant.REGISTRY_MAPPING);
        byte[] serialize = serializerService.serialize(success);
        FullHttpResponse fullHttpResponse = HttpUtils.buildFullHttpResponse(serialize, null);
        channel.writeAndFlush(fullHttpResponse).sync();
    }
}
