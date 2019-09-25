package admin.core.netty.server;

import admin.core.netty.server.handler.HeartBeatCommandHandler;
import admin.core.netty.server.handler.NotifyCommandHandler;
import admin.core.netty.server.handler.RegistryCommandHandler;
import com.google.common.collect.Maps;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;
import tesseract.core.dto.TesseractExecutorResponse;
import tesseract.core.netty.HandleBean;
import tesseract.core.netty.ICommandHandler;
import tesseract.core.util.HttpUtils;

import java.util.Map;

import static tesseract.core.constant.CommonConstant.*;

@Slf4j
@ChannelHandler.Sharable
public class NettyServerCommandDispatcher extends ChannelInboundHandlerAdapter {
    private static final Map<String, ICommandHandler> COMMAND_HANDLER_MAP = Maps.newHashMap();

    static {
        init();
    }

    /**
     * 初始化处理器
     */
    private static void init() {
        COMMAND_HANDLER_MAP.put(REGISTRY_MAPPING, new RegistryCommandHandler());
        COMMAND_HANDLER_MAP.put(NOTIFY_MAPPING, new NotifyCommandHandler());
        COMMAND_HANDLER_MAP.put(HEARTBEAT_MAPPING, new HeartBeatCommandHandler());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
        log.info("接收到请求:{}", fullHttpRequest);
        String uri = fullHttpRequest.uri();
        String path = HttpUtils.buildURLPath(uri);
        ICommandHandler iCommandHandler = COMMAND_HANDLER_MAP.get(path);
        if (iCommandHandler == null) {
            log.error("找不到处理器,path:{}", path);
            byte[] serialize = TesseractJobServiceDelegator.serializerService.serialize(TesseractExecutorResponse.FAIL);
            FullHttpResponse fullHttpResponse = HttpUtils.buildFullHttpResponse(serialize, null);
            ctx.writeAndFlush(fullHttpResponse).sync();
            return;
        }
        HandleBean handleBean = new HandleBean();
        handleBean.setData(fullHttpRequest.content());
        handleBean.setUrl(uri);
        iCommandHandler.handleCommand(handleBean, ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("客户端断开连接:{},异常信息:{}", ctx.channel(), cause.getMessage());
    }
}
