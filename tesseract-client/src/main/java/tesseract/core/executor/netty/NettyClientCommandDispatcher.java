package tesseract.core.executor.netty;

import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;
import tesseract.core.dto.TesseractExecutorResponse;
import tesseract.core.executor.ClientServiceDelegator;
import tesseract.core.executor.netty.handler.ClientHeartBeatHandler;
import tesseract.core.executor.netty.handler.ClientRegistryHandler;
import tesseract.core.netty.HandleBean;
import tesseract.core.netty.ICommandHandler;

import java.util.Map;

import static tesseract.core.constant.CommonConstant.HEARTBEAT_MAPPING;
import static tesseract.core.constant.CommonConstant.REGISTRY_MAPPING;

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
        COMMAND_HANDLER_MAP.put(REGISTRY_MAPPING, new ClientRegistryHandler());
        COMMAND_HANDLER_MAP.put(HEARTBEAT_MAPPING, new ClientHeartBeatHandler());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpResponse httpResponse = (FullHttpResponse) msg;
        ByteBuf content = httpResponse.content();
        byte[] bytes = new byte[content.readableBytes()];
        content.readBytes(bytes);
        TesseractExecutorResponse executorResponse = (TesseractExecutorResponse) ClientServiceDelegator.serializerService.deserialize(bytes);
        String handlerPath = executorResponse.getHandlerPath();
        ICommandHandler iCommandHandler = COMMAND_HANDLER_MAP.get(handlerPath);
        if (iCommandHandler == null) {
            log.error("找不到对应执行器");
            return;
        }
        HandleBean handleBean = new HandleBean();
        handleBean.setData(executorResponse);
        iCommandHandler.handleCommand(handleBean, ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        log.error("NettyClientCommandDispatcher 发生异常:{}", cause.getMessage());
        ctx.fireExceptionCaught(cause);
    }
}
