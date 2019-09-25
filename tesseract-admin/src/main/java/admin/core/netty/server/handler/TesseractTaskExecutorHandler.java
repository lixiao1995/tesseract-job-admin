package admin.core.netty.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static admin.core.netty.server.TesseractJobServiceDelegator.CHANNEL_MAP;

/**
 * @description:
 * @author: nickle
 * @create: 2019-09-25 16:14
 **/
@ChannelHandler.Sharable
@Slf4j
@AllArgsConstructor
public class TesseractTaskExecutorHandler extends ChannelInboundHandlerAdapter {
    private String socket;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.toString());
        CHANNEL_MAP.remove(socket);
    }
}
