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
     * @param batchSize 页数
     * @param time 下次触发时间
     * @param timeWindowSize 时间窗口
     * @author wangzhe01@Koolearn-inc.com
     * @date 2019/9/16 18:15
     * @return list
     */
    List<TesseractTrigger> findTriggerWithLock(TesseractGroup tesseractGroup, int batchSize, long time, Integer timeWindowSize);

    TriggerVO listByPage(Integer currentPage, Integer pageSize,
                         TesseractTrigger condition,
                         Long startCreateTime,
                         Long endCreateTime);

    void executeTrigger(String groupName, Integer triggerId);

    void startTrigger(Integer triggerId) throws ParseException;

    void stopTrigger(Integer triggerId);

    void deleteTrigger(Integer triggerId);

    void saveOrUpdateTrigger(TesseractTrigger tesseractTrigger) throws Exception;


    boolean resovleMissfireTrigger(Integer pageSize, Long time);
}
