package tesseract.core.executor;

import tesseract.core.annotation.ClientJobDetail;
import tesseract.core.netty.NettyClient;
import tesseract.core.serializer.ISerializerService;
import tesseract.core.executor.service.IClientService;

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
}
