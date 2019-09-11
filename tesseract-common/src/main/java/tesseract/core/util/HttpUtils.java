package tesseract.core.util;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;

import java.net.URI;
import java.util.function.Consumer;

/**
 * @description: http 操作工具类
 * @author: nickle
 * @create: 2019-09-10 10:00
 **/
public class HttpUtils {
    public static FullHttpResponse buildFullHttpResponse(byte[] content, Consumer<FullHttpResponse> callback) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK
                , Unpooled.copiedBuffer(content));
        response.headers().add(HttpHeaderNames.CONTENT_TYPE,HttpHeaderValues.BINARY);
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH,content.length);
        if (callback != null) {
            callback.accept(response);
        }
        return response;
    }

    public static FullHttpRequest buildFullHttpRequest(URI uri, byte[] content, Consumer<FullHttpRequest> callback) {
        DefaultFullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
                uri.toASCIIString(), Unpooled.wrappedBuffer(content));
        if (callback != null) {
            callback.accept(httpRequest);
        }
        return httpRequest;
    }

    public static String buildURLPath(String url) {
        return url.substring(url.lastIndexOf("/"));
    }
}
