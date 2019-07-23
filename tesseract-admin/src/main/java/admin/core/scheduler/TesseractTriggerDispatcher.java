package admin.core.scheduler;

import admin.core.scheduler.pool.ISchedulerThreadPool;
import admin.entity.*;
import admin.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.eventbus.EventBus;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Data
public class TesseractTriggerDispatcher {

    private String groupName;
    private ITesseractJobDetailService tesseractJobDetailService;
    private ITesseractExecutorDetailService executorDetailService;
    private ITesseractExecutorService executorService;
    private ISchedulerThreadPool threadPool;
    private SendToExecute sendToExecute;
    private EventBus retryEventBus;


    public ISchedulerThreadPool getThreadPool() {
        return threadPool;
    }

    public void dispatchTrigger(List<TesseractTrigger> triggerList, boolean isOnce) {
        triggerList.stream().forEach(trigger -> threadPool.runJob(new TaskRunnable(trigger, isOnce, sendToExecute)));
    }

    public int blockGetAvailableThreadNum() {
        return threadPool.blockGetAvailableThreadNum();
    }

    public void init() {
        threadPool.init();
    }

    private class TaskRunnable implements Runnable {
        private TesseractTrigger trigger;
        private boolean isOnce;
        private SendToExecute sendToExecute;

        public TaskRunnable(TesseractTrigger trigger, boolean isOnce, SendToExecute sendToExecute) {
            this.trigger = trigger;
            this.isOnce = isOnce;
            this.sendToExecute = sendToExecute;
        }


        @Override
        public void run() {
            try {
                //获取job detail
                TesseractJobDetail jobDetail = getJobDetail();
                if (jobDetail == null) {
                    sendToExecute.doFail("没有发现可运行job", trigger);
                    return;
                }
                //获取执行器
                TesseractExecutor executor = executorService.getById(trigger.getExecutorId());
                if (executor == null) {
                    sendToExecute.doFail("没有找到可用执行器", trigger);
                    return;
                }
                //执行器下机器列表
                List<TesseractExecutorDetail> executorDetailList = getExecutorDetail(executor.getId());
                if (CollectionUtils.isEmpty(executorDetailList)) {
                    sendToExecute.doFail("执行器下没有可用机器", trigger);
                    return;
                }
                //路由发送执行
                sendToExecute.routerExecute(jobDetail, executorDetailList, trigger);
            } catch (Exception e) {
                e.printStackTrace();
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
            return tesseractJobDetailService.getOne(jobQueryWrapper);
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
            return executorDetailService.list(executorDetailQueryWrapper);
        }


    }

    public void stop() {
        threadPool.shutdown();
    }
}