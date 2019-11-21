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
    /**
     * 执行心跳操作，更新executor detail update time
     *
     * @param heartBeatRequest
     */
    void heartBeat(TesseractHeartbeatRequest heartBeatRequest);

    /**
     * 清除失效机器
     *
     * @param tesseractGroup
     * @param pageSize
     * @param time           失效时间
     * @return
     * @throws Exception
     */
    boolean clearInvalidMachine(TesseractGroup tesseractGroup, Integer pageSize, Long time) throws Exception;


}
