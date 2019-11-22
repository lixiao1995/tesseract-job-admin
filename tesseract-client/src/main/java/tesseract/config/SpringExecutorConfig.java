package tesseract.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tesseract.Constant;
import tesseract.core.annotation.ClientJobDetail;
import tesseract.core.executor.TesseractExecutor;
import tesseract.core.executor.service.IClientService;
import tesseract.core.executor.service.NettyClientServiceImpl;
import tesseract.core.serializer.HessianSerializerService;
import tesseract.core.serializer.ISerializerService;

import java.util.List;

@Configuration
public class SpringExecutorConfig {

    @Value("${tesseract.admin.address}")
    private String adminServerAddress;
    @Autowired
    private List<ClientJobDetail> clientJobDetailList;

    @Value("${tesseract.netty.server.port}")
    private int nettyServerPort;

    @Value("${tesseract.executor.pool.corePoolSize:0}")
    private int corePoolSize;
    @Value("${tesseract.executor.pool.maxPoolSize:0}")
    private int maxPoolSize;
    @Value("${tesseract.executor.pool.queueSize:0}")
    private int queueSize;


    @Bean(initMethod = "init", destroyMethod = "destroy")
    public TesseractExecutor tesseractExecutor(IClientService clientFeignService, ISerializerService serializerService) {
        TesseractConfiguration.TesseractConfigurationBuilder builder = TesseractConfiguration.builder();
        builder.adminServerAddress(adminServerAddress)
                .clientFeignService(clientFeignService)
                .nettyServerPort(nettyServerPort)
                .serializerService(serializerService)
                .clientJobDetailList(clientJobDetailList)
                .corePoolSize(Constant.DEFAULT_MIN_POOL_SIZE)
                .maxPoolSize(Constant.DEFAULT_MAX_POOL_SIZE)
                .queueSize(Constant.DEFAULT_QUEUE_SIZE);
        TesseractConfiguration configuration = builder.build();
        if (corePoolSize > 0) {
            configuration.setCorePoolSize(corePoolSize);
        }
        if (maxPoolSize > 0) {
            configuration.setMaxPoolSize(maxPoolSize);
        }
        if (queueSize > 0) {
            configuration.setQueueSize(queueSize);
        }
        return new TesseractExecutor(configuration);
    }

    @Bean
    public IClientService clientService() {
        return new NettyClientServiceImpl();
    }

    @Bean
    public ISerializerService serializerService() {
        return new HessianSerializerService();
    }
}
