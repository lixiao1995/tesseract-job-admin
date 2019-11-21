package admin.service;

import admin.entity.TesseractGroup;
import admin.entity.TesseractTrigger;
import admin.pojo.VO.TriggerVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.text.ParseException;
import java.util.List;

/**
 * <p>触发器服务类</p>
 *
 * @author nickle
 * @since 2019-07-03
 */
public interface ITesseractTriggerService extends IService<TesseractTrigger> {

    /**
     * <p>Title: findTriggerWithLock</p>
     * <p>获取触发器</p>
     *
     * @param tesseractGroup 触发器组
     * @param batchSize      页数
     * @param time           下次触发时间
     * @param timeWindowSize 时间窗口
     * @return list
     * @author wangzhe01@Koolearn-inc.com
     * @date 2019/9/16 18:15
     */
    List<TesseractTrigger> findTriggerWithLock(TesseractGroup tesseractGroup, int batchSize, long time, Integer timeWindowSize);

    TriggerVO listByPage(Integer currentPage, Integer pageSize,
                         TesseractTrigger condition,
                         Long startCreateTime,
                         Long endCreateTime);

    /**
     * 手动触发任务执行一次
     *
     * @param groupId
     * @param triggerId
     */
    void executeTrigger(Integer groupId, Integer triggerId);

    /**
     * 启动触发器
     *
     * @param triggerId
     * @throws ParseException
     */
    void startTrigger(Integer triggerId) throws ParseException;

    /**
     * 停止触发器
     *
     * @param triggerId
     */
    void stopTrigger(Integer triggerId);

    /**
     * 删除触发器
     *
     * @param triggerId
     */
    void deleteTrigger(Integer triggerId);

    /**
     * 保存或更新触发器
     *
     * @param tesseractTrigger
     * @throws Exception
     */
    void saveOrUpdateTrigger(TesseractTrigger tesseractTrigger) throws Exception;

    /**
     * 解析错过执行时间的触发器
     *
     * @param tesseractGroup
     * @param pageSize
     * @param time           临界时间
     * @return
     */
    boolean resovleMissfireTrigger(TesseractGroup tesseractGroup, Integer pageSize, Long time);
}
