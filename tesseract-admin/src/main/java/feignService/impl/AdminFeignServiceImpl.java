package feignService.impl;

import admin.core.netty.server.TesseractJobServiceDelegator;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;
import tesseract.core.constant.CommonConstant;
import tesseract.core.dto.TesseractExecutorRequest;
import tesseract.core.dto.TesseractExecutorResponse;
import feignService.IAdminFeignService;
import tesseract.core.serializer.ISerializerService;
import tesseract.core.util.HttpUtils;
import tesseract.exception.TesseractException;

import java.net.URI;
import java.util.Map;

/**
 * <p>Title AdminFeignServiceImpl </p>
 * <p> </p>
 * <p>Company: http://www.koolearn.com </p>
 *
 * @author wangzhe01@Koolearn-inc.com
 * @date 2019/9/11 15:04
 */
@Slf4j
public class AdminFeignServiceImpl implements IAdminFeignService {

    @Override
    public TesseractExecutorResponse sendToExecutor(URI uri, TesseractExecutorRequest request) throws InterruptedException {
        String socket = uri.getHost() + ":" + uri.getPort();
        Map<String, Channel> channelMap = TesseractJobServiceDelegator.getChannelMap();
        if (!channelMap.containsKey(socket)) {
            log.error("sendToExecutor, channelMap not contain key:{}", socket);
            throw new TesseractException("没有可用channel");
        }

        Channel channel = channelMap.get(socket);
        if (!channel.isActive()) {
            log.error("sendToExecutor, channel is not active!");
            throw new TesseractException("当前channel不可用");
        }

        // 发送调度请求
        ISerializerService serializerService = TesseractJobServiceDelegator.getSerializerService();
        TesseractExecutorResponse response = new TesseractExecutorResponse(TesseractExecutorResponse.SUCCESS_STATUS, request, CommonConstant.EXECUTE_MAPPING);
        byte[] serialize = serializerService.serialize(response);
        FullHttpResponse httpResponse = HttpUtils.buildFullHttpResponse(serialize, null);
        channel.writeAndFlush(httpResponse).sync();

        return TesseractExecutorResponse.SUCCESS;
    }

    @Override
    public void failCallBack() {
        // todo: 触发检测执行器可用线程
        System.out.println("fail!");
    }

}
