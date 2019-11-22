package tesseract.core.executor.netty.client.handler;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import tesseract.core.dto.TesseractExecutorResponse;
import tesseract.core.executor.ClientServiceDelegator;
import tesseract.core.executor.TesseractExecutor;
import tesseract.core.netty.HandleBean;
import tesseract.core.netty.ICommandHandler;

/**
 * @description: 心跳处理器
 * @author: nickle
 * @create: 2019-09-09 12:14
 **/
@Slf4j
public class ClientHeartBeatHandler implements ICommandHandler {

    @Override
    public void handleCommand(HandleBean handleBean, Channel channel) throws Exception {
        TesseractExecutorResponse executorResponse = (TesseractExecutorResponse) handleBean.getData();
        if (executorResponse.getStatus() == TesseractExecutorResponse.SUCCESS_STATUS) {
            log.info("心跳成功");
            return;
        }
        ClientServiceDelegator.tesseractExecutor.pingPongThread.startRegistry();
    }
}
