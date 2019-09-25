package tesseract.core.executor;

import tesseract.core.annotation.ClientJobDetail;
import tesseract.core.executor.netty.NettyClientCommandDispatcher;
import tesseract.core.executor.service.IClientService;
import tesseract.core.netty.NettyClient;
import tesseract.core.serializer.ISerializerService;

import java.net.URI;
import java.util.List;

/**
 * @description: 客户端服务代理
 * @author: nickle
 * @create: 2019-09-24 15:34
 **/
public class ClientServiceDelegator {
    public static Integer nettyServerPort;

    public static IClientService clientFeignService;

    public static String adminServerAddress;

    public static List<ClientJobDetail> clientJobDetailList;

    public static ISerializerService serializerService;

    public static NettyClient nettyClient;

    public static TesseractExecutor tesseractExecutor;

    /**
     * 只有一个注册线程调用，线程安全
     *
     * @return
     */
    public static NettyClient getNettyClient() {
        if (nettyClient == null) {
            URI uri = URI.create(adminServerAddress);
            nettyClient = new NettyClient(uri.getHost(), uri.getPort(), new NettyClientCommandDispatcher());
        }
        return nettyClient;
    }
}
