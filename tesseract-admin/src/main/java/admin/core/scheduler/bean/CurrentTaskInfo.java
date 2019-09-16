package admin.core.scheduler.bean;

import admin.entity.TesseractExecutorDetail;
import admin.entity.TesseractFiredJob;
import admin.entity.TesseractLog;
import lombok.Data;
import tesseract.core.dto.TesseractExecutorRequest;

/**
 * @description: 当前正在执行的bean信息
 * @author: nickle
 * @create: 2019-09-16 15:44
 **/
@Data
public class CurrentTaskInfo {
    /**
     * 任务上下文
     */
    private TaskContextInfo taskContextInfo;
    /**
     * 日志
     */
    private TesseractLog log;
    /**
     * 当前请求
     */
    private TesseractExecutorRequest executorRequest;
    /**
     * 正在执行的任务
     */
    private TesseractFiredJob firedJob;

    /**
     * 当前执行器
     */
    private TesseractExecutorDetail currentExecutorDetail;

    /**
     * 分片索引
     */
    private Integer shardingIndex;

    public CurrentTaskInfo(TaskContextInfo taskContextInfo) {
        this.taskContextInfo = taskContextInfo;
    }
}
