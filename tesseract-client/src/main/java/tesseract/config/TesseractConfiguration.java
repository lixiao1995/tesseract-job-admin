package tesseract.config;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import tesseract.Constant;
import tesseract.core.annotation.ClientJobDetail;
import tesseract.core.executor.TesseractExecutor;
import tesseract.core.executor.service.IClientService;
import tesseract.core.serializer.ISerializerService;

import java.util.List;

/**
 * 默认配置类，用于非spring启动
 *
 * @author nickle
 */
@Data
@Accessors(chain = true)
@Builder
public class TesseractConfiguration {

    private IClientService clientFeignService;
    private String adminServerAddress;
    private List<ClientJobDetail> clientJobDetailList;
    private ISerializerService serializerService;
    private int nettyServerPort;


    private int corePoolSize;
    private int maxPoolSize;
    private int queueSize;

}
