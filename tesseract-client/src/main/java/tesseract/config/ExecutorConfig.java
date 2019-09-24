package tesseract.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tesseract.core.executor.TesseractExecutor;
import tesseract.core.executor.service.IClientService;
import tesseract.core.executor.service.NettyClientServiceImpl;
import tesseract.core.serializer.HessianSerializerService;
import tesseract.core.serializer.ISerializerService;

@Configuration
public class ExecutorConfig {

    @Bean(initMethod = "init", destroyMethod = "destroy")
    public TesseractExecutor tesseractExecutor() {
        return new TesseractExecutor();
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
