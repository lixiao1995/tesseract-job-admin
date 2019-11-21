package admin.service;

import admin.entity.TesseractToken;
import admin.pojo.DO.StatisticsLogDO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author nickle
 * @since 2019-07-06
 */
public interface ITesseractTokenService extends IService<TesseractToken> {
    /**
     * 分析活跃用户，目前没有使用
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @Deprecated
    List<StatisticsLogDO> statisticsActiveUser(long startTime, long endTime);
}
