package tesseract.core.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * @description: 公共工具类
 * @author: nickle
 * @create: 2019-09-11 10:08
 **/
@Slf4j
public class CommonUtils {
    public static byte[] byteBufToByteArr(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        byteBuf.release();
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

    public static boolean checkListItem(List<?> arr, String fieldName, Object value) {
        try {
            for (Object object : arr) {
                Class<?> aClass = object.getClass();
                Field field = aClass.getField(fieldName);
                field.setAccessible(true);
                if (field.get(object).equals(value)) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
        return false;
    }
}
