package tesseract.service.netty.handler;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import tesseract.core.dto.TesseractExecutorRequest;
import tesseract.core.dto.TesseractExecutorResponse;
import tesseract.core.executor.TesseractExecutor;
import tesseract.core.netty.HandleBean;
import tesseract.core.netty.ICommandHandler;
import tesseract.service.netty.NettyHttpClient;

/**
 * <p>Title ClientExecuteHandler </p>
 * <p> </p>
 * <p>Company: http://www.koolearn.com </p>
 *
 * @author wangzhe01@Koolearn-inc.com
 * @date 2019/9/11 16:21
 */
@Slf4j
public class ClientExecuteHandler implements ICommandHandler {

    @Override
    public void handleCommand(HandleBean handleBean, Channel channel) throws Exception {
        TesseractExecutorResponse response = (TesseractExecutorResponse) handleBean.getData();
        TesseractExecutorRequest request = (TesseractExecutorRequest) response.getBody();
        NettyHttpClient.tesseractExecutor.execute(request);
    }
}
