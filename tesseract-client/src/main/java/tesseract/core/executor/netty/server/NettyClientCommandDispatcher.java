package tesseract.core.executor.netty.server;

import com.google.common.collect.Maps;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;
import tesseract.core.dto.TesseractExecutorResponse;
import tesseract.core.executor.ClientServiceDelegator;
import tesseract.core.executor.netty.server.handler.ExecuteTaskCommandHandler;
import tesseract.core.executor.netty.server.handler.StopTaskCommandHandler;
import tesseract.core.netty.HandleBean;
import tesseract.core.netty.ICommandHandler;
import tesseract.core.util.HttpUtils;

import java.util.Map;

import static tesseract.core.constant.CommonConstant.EXECUTE_MAPPING;
import static tesseract.core.constant.CommonConstant.STOP_MAPPING;

/**
 * http server 接收http 请求并分发
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyClientCommandDispatcher extends ChannelInboundHandlerAdapter {
    private static final Map<String, ICommandHandler> COMMAND_HANDLER_MAP = Maps.newHashMap();

    static {
        init();
    }

    /**
     * 初始化处理器
     */
    private static void init() {
        COMMAND_HANDLER_MAP.put(EXECUTE_MAPPING, new ExecuteTaskCommandHandler());
        COMMAND_HANDLER_MAP.put(STOP_MAPPING, new StopTaskCommandHandler());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
        String uri = fullHttpRequest.uri();
        String path = HttpUtils.getURLPath(uri);
        ICommandHandler iCommandHandler = COMMAND_HANDLER_MAP.get(path);
        if (iCommandHandler == null) {
            log.error("找不到处理器,path:{}", path);
            TesseractExecutorResponse executorResponse = TesseractExecutorResponse.FAIL;
            executorResponse.setBody(String.format("找不到处理器,path:%s", path));
            FullHttpResponse httpResponse = HttpUtils.buildFullHttpResponse(ClientServiceDelegator.serializerService.serialize(executorResponse),
                    null);
            ctx.writeAndFlush(httpResponse).sync();
            return;
        }
        HandleBean handleBean = new HandleBean();
        handleBean.setData(fullHttpRequest.content());
        handleBean.setUrl(uri);
        iCommandHandler.handleCommand(handleBean, ctx.channel());
    }
}
