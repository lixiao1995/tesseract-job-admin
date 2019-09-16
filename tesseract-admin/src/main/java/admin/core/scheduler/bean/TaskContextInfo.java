package admin.core.scheduler.bean;

import admin.entity.TesseractExecutorDetail;
import admin.entity.TesseractJobDetail;
import admin.entity.TesseractTrigger;
import lombok.Data;

import java.util.List;

/**
 * @description: 执行任务上下文信息，保存执行任务需要的bean
 * @author: nickle
 * @create: 2019-09-16 14:25
 **/
@Data
public class TaskContextInfo {
    /**
     * 任务详情
     */
    private TesseractJobDetail jobDetail;
    /**
     * 执行器列表
     */
    private List<TesseractExecutorDetail> executorDetailList;
    /**
     * 触发器
     */
    private TesseractTrigger trigger;
}
