package tesseract.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tesseract.controller.ExecutorController;
import tesseract.core.executor.TesseractExecutor;
import tesseract.core.serializer.HessianSerializerService;
import tesseract.core.serializer.ISerializerService;
import tesseract.service.IClientService;
import tesseract.service.netty.NettyClientService;

@Configuration
public class ExecutorConfig {


    @Bean
    public ExecutorController executorController() {
        return new ExecutorController();
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    public TesseractExecutor tesseractExecutor() {
        return new TesseractExecutor();
    }

    @Bean
    public IClientService clientService() {
        return new NettyClientService(new HessianSerializerService());
    }

    @Bean
    public ISerializerService serializerService() {
        return new HessianSerializerService();
    }
}
