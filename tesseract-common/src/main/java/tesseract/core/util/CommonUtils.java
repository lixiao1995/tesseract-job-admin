package tesseract.core.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;

/**
 * @description: 公共工具类
 * @author: nickle
 * @create: 2019-09-11 10:08
 **/
public class CommonUtils {
    public static byte[] byteBufToByteArr(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        return bytes;
    }

    public static String buildSocket(Channel channel) {
        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        String hostName = socketAddress.getHostName();
        int port = socketAddress.getPort();
        return hostName + ":" + port;
    }

    public static String buildSocket(Channel channel, int port) {
        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        String hostName = socketAddress.getHostName();
        return hostName + ":" + port;
    }
}
