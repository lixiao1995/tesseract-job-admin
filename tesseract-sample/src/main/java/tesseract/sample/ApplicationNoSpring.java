package tesseract.sample;

import com.google.common.collect.Lists;
import tesseract.Constant;
import tesseract.config.TesseractConfiguration;
import tesseract.core.ClientJobDetailBuilder;
import tesseract.core.annotation.ClientJobDetail;
import tesseract.core.executor.TesseractExecutor;
import tesseract.core.executor.service.NettyClientServiceImpl;
import tesseract.core.serializer.HessianSerializerService;

import java.util.List;

/**
 * 非spring工程使用
 */
public class ApplicationNoSpring {
    public static void main(String[] args) {
        List<ClientJobDetail> clientJobDetailList = Lists.newArrayList();
        ClientJobDetail jobDetail = ClientJobDetailBuilder.build(TestJob.class);
        clientJobDetailList.add(jobDetail);
        TesseractConfiguration.TesseractConfigurationBuilder builder = TesseractConfiguration.builder();
        builder.adminServerAddress("adminServerAddress")
                .clientFeignService(new NettyClientServiceImpl())
                .nettyServerPort(8087)
                .serializerService(new HessianSerializerService())
                .clientJobDetailList(clientJobDetailList)
                .corePoolSize(Constant.DEFAULT_MIN_POOL_SIZE)
                .maxPoolSize(Constant.DEFAULT_MAX_POOL_SIZE)
                .queueSize(Constant.DEFAULT_QUEUE_SIZE);
        TesseractConfiguration configuration = builder.build();
        TesseractExecutor tesseractExecutor = new TesseractExecutor(configuration);
        tesseractExecutor.init();
    }
}
