package admin.core.scheduler;

import admin.core.TesseractJobServiceDelegator;
import admin.core.scheduler.bean.TaskContextInfo;
import admin.core.scheduler.pool.ISchedulerThreadPool;
import admin.entity.TesseractExecutor;
import admin.entity.TesseractExecutorDetail;
import admin.entity.TesseractJobDetail;
import admin.entity.TesseractTrigger;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author: nickle
 * @description: 触发器分发器，在这里执行任务的分发
 * @date: 2019-07-24 16:01
 */
@Slf4j
@Data
public class TesseractTriggerDispatcher {
    private String groupName;
    private ISchedulerThreadPool threadPool;

    public ISchedulerThreadPool getThreadPool() {
        return threadPool;
    }

    public void dispatchTrigger(List<TesseractTrigger> triggerList) {
        triggerList.stream().forEach(trigger -> threadPool.runJob(new TaskRunnable(trigger)));
    }

    public int blockGetAvailableThreadNum() {
        return threadPool.blockGetAvailableThreadNum();
    }

    public void init() {
        threadPool.init();
    }

    /**
     * @author: nickle
     * @description: 任务执行体
     * @date: 2019-07-24 16:01
     */
    private class TaskRunnable implements Runnable {
        private TesseractTrigger trigger;

        public TaskRunnable(TesseractTrigger trigger) {
            this.trigger = trigger;
        }

        @Override
        public void run() {
            TaskContextInfo taskContextInfo = TesseractBeanFactory.createTaskContextInfo(null, null, trigger);
            try {
                //获取job detail
                TesseractJobDetail jobDetail = getJobDetail();
                if (jobDetail == null) {
                    log.error("没有发现可运行job");
                    TaskExecutorDelegate.doFail("没有发现可运行job", taskContextInfo);
                    return;
                }
                taskContextInfo.setJobDetail(jobDetail);
                //获取执行器
                TesseractExecutor executor = TesseractJobServiceDelegator.executorService.getById(trigger.getExecutorId());
                if (executor == null) {
                    log.error("没有找到可用执行器");
                    TaskExecutorDelegate.doFail("没有找到可用执行器", taskContextInfo);
                    return;
                }
                //执行器下机器列表
                List<TesseractExecutorDetail> executorDetailList = getExecutorDetail(executor.getId());
                if (CollectionUtils.isEmpty(executorDetailList)) {
                    log.error("执行器下没有可用机器");
                    TaskExecutorDelegate.doFail("执行器下没有可用机器", taskContextInfo);
                    return;
                }
                taskContextInfo.setExecutorDetailList(executorDetailList);
                //路由发送执行
                log.info("任务上下文信息:{}", taskContextInfo);
                TaskExecutorDelegate.routerExecute(taskContextInfo);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("任务执行异常:{},上下文信息:{}", e.toString(), taskContextInfo);
                TaskExecutorDelegate.doFail("发生未知异常", taskContextInfo);
            }
        }


        /**
         * 获取当前触发器可执行的任务
         * 一个触发器只能对应一个任务!!!!!!!!!!!
         *
         * @return
         */
        private TesseractJobDetail getJobDetail() {
            QueryWrapper<TesseractJobDetail> jobQueryWrapper = new QueryWrapper<>();
            jobQueryWrapper.lambda().eq(TesseractJobDetail::getTriggerId, trigger.getId());
            return TesseractJobServiceDelegator.jobDetailService.getOne(jobQueryWrapper);
        }


        /**
         * 获取可执行的机器
         *
         * @param executorId
         * @return
         */
        private List<TesseractExecutorDetail> getExecutorDetail(Integer executorId) {
            QueryWrapper<TesseractExecutorDetail> executorDetailQueryWrapper = new QueryWrapper<>();
            executorDetailQueryWrapper.lambda().eq(TesseractExecutorDetail::getExecutorId, executorId);
            return TesseractJobServiceDelegator.executorDetailService.list(executorDetailQueryWrapper);
        }


    }

    public void stop() {
        threadPool.shutdown();
    }
}