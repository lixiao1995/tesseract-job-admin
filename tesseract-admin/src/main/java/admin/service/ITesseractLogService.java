package admin.service;

import admin.entity.TesseractLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import tesseract.core.dto.TesseractAdminJobNotify;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author nickle
 * @since 2019-07-03
 */
public interface ITesseractLogService extends IService<TesseractLog> {
    /**
     * 任务执行完毕，通知调度端结果
     *
     * @param tesseractAdminJobNotify
     */
    void notify(TesseractAdminJobNotify tesseractAdminJobNotify);

    /**
     * 分页获取日志
     *
     * @param currentPage
     * @param pageSize
     * @param condition
     * @param startCreateTime
     * @param endCreateTime
     * @param startUpdateTime
     * @param endUpdateTime
     * @return
     */
    IPage<TesseractLog> listByPage(Integer currentPage, Integer pageSize, TesseractLog condition, Long startCreateTime,
                                   Long endCreateTime,
                                   Long startUpdateTime,
                                   Long endUpdateTime);

    /**
     * 获取日志线形图数据
     *
     * @return
     */
    Map<String, Collection<Integer>> statisticsLogLine();

    /**
     * 获取日志饼图数据
     *
     * @return
     */
    List<Map<String, Object>> statisticsLogPie();
}
