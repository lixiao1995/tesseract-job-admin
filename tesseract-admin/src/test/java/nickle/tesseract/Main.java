package nickle.tesseract;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @description:
 * @author: nickle
 * @create: 2019-09-09 14:14
 **/
public class Main {
    public static void main(String[] args) throws URISyntaxException, InterruptedException, UnsupportedEncodingException {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new HttpRequestEncoder());//客户端对发送的httpRequest进行编码
                            socketChannel.pipeline().addLast(new HttpResponseDecoder());//客户端需要对服务端返回的httpresopnse解码
                            // socketChannel.pipeline().addLast(new HttpClientCodec());//HttpClientCodec()包含了上面两种
                            socketChannel.pipeline().addLast(new HttpClientDealing());
                        }
                    });

            Channel channel = bootstrap.connect("127.0.0.1", 8080).sync().channel();

            URI uri = new URI("http://127.0.0.1:8080");
            DefaultFullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET
                    , uri.toASCIIString(), Unpooled.wrappedBuffer("msg".getBytes("UTF-8")));//生成一个默认的httpRequest。

            httpRequest.headers().set(HttpHeaders.Names.HOST, "127.0.0.1");
            httpRequest.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            httpRequest.headers().set(HttpHeaders.Names.CONTENT_LENGTH, httpRequest.content().readableBytes());//可以在httpRequest.headers中设置各种需要的信息。

            channel.writeAndFlush(httpRequest).sync();//发送

            channel.closeFuture().sync();

        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    private static class HttpClientDealing extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof HttpResponse) {
                System.out.println(msg.toString());//打印服务器返回的httpResponse
            }

            if (msg instanceof HttpContent) {
                System.out.println(msg.toString());
            }
        }
    }
}
