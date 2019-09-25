package admin.service;

import admin.entity.TesseractExecutorDetail;
import admin.entity.TesseractGroup;
import com.baomidou.mybatisplus.extension.service.IService;
import tesseract.core.dto.TesseractHeartbeatRequest;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author nickle
 * @since 2019-07-07
 */
public interface ITesseractExecutorDetailService extends IService<TesseractExecutorDetail> {
    void heartBeat(TesseractHeartbeatRequest heartBeatRequest);

    boolean clearInvalidMachine(TesseractGroup tesseractGroup,Integer pageSize, Long time) throws Exception;


}
