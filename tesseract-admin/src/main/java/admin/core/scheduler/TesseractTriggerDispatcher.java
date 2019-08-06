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

/**
 * @author: huangjun
 * @description:   触发器
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-24 16:01
 */
@Slf4j
@Data
public class TesseractTriggerDispatcher {

    private String groupName;
    private ITesseractJobDetailService tesseractJobDetailService;
    private ITesseractExecutorDetailService executorDetailService;
    private ITesseractExecutorService executorService;
    private ISchedulerThreadPool threadPool;
    private SenderDelegate senderDelegate;
    private EventBus retryEventBus;


    public ISchedulerThreadPool getThreadPool() {
        return threadPool;
    }

    public void dispatchTrigger(List<TesseractTrigger> triggerList, boolean isOnce) {
        triggerList.stream().forEach(trigger -> threadPool.runJob(new TaskRunnable(trigger, isOnce, senderDelegate)));
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
        private SenderDelegate senderDelegate;

        public TaskRunnable(TesseractTrigger trigger, boolean isOnce, SenderDelegate senderDelegate) {
            this.trigger = trigger;
            this.isOnce = isOnce;
            this.senderDelegate = senderDelegate;
        }


        @Override
        public void run() {
            try {
                //获取job detail
                TesseractJobDetail jobDetail = getJobDetail();
                if (jobDetail == null) {
                    senderDelegate.doFail("没有发现可运行job", trigger,jobDetail);
                    return;
                }
                //获取执行器
                TesseractExecutor executor = executorService.getById(trigger.getExecutorId());
                if (executor == null) {
                    senderDelegate.doFail("没有找到可用执行器", trigger,jobDetail);
                    return;
                }
                //执行器下机器列表
                List<TesseractExecutorDetail> executorDetailList = getExecutorDetail(executor.getId());
                if (CollectionUtils.isEmpty(executorDetailList)) {
                    senderDelegate.doFail("执行器下没有可用机器", trigger,jobDetail);
                    return;
                }
                //路由发送执行
                senderDelegate.routerExecute(jobDetail, executorDetailList, trigger);
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