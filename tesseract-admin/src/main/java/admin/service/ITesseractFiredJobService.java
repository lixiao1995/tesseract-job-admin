package admin.service;

import admin.entity.TesseractFiredJob;
import admin.pojo.VO.FiredTriggerVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.net.URISyntaxException;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author nickle
 * @since 2019-07-18
 */
public interface ITesseractFiredJobService extends IService<TesseractFiredJob> {
    /**
     * 分页查询当前正在运行的执行器
     *
     * @param currentPage
     * @param pageSize
     * @param condition
     * @return
     */
    FiredTriggerVO findFiredTrigger(Long currentPage, Long pageSize, TesseractFiredJob condition);

    /**
     * 停止当前正在执行的job
     *
     * @param firedTriggerId
     */
    void stopFiredJob(Integer firedTriggerId) throws URISyntaxException, Exception;
}
