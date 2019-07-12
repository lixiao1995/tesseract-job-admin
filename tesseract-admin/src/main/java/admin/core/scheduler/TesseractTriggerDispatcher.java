package admin.core.scheduler;

import admin.constant.AdminConstant;
import admin.core.event.MailEvent;
import admin.core.mail.TesseractMailTemplate;
import admin.core.scheduler.pool.ISchedulerThreadPool;
import admin.core.scheduler.router.impl.HashRouter;
import admin.entity.*;
import admin.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import feignService.IAdminFeignService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import tesseract.core.dto.TesseractExecutorRequest;
import tesseract.core.dto.TesseractExecutorResponse;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import static admin.constant.AdminConstant.*;
import static tesseract.core.constant.CommonConstant.EXECUTE_MAPPING;
import static tesseract.core.constant.CommonConstant.HTTP_PREFIX;

@Slf4j
@Data
public class TesseractTriggerDispatcher {
    private static final String LOG_TEMPLATE_NAME = "logTemplate.html";
    private static final String LOG_SUBJECT = "Tesseract-job日志报警邮件";
    private String groupName;
    private ITesseractJobDetailService tesseractJobDetailService;
    private ITesseractLogService tesseractLogService;
    private ITesseractGroupService groupService;
    private ITesseractExecutorDetailService executorDetailService;
    private ITesseractExecutorService executorService;
    private ITesseractFiredTriggerService firedTriggerService;
    private IAdminFeignService feignService;
    private ISchedulerThreadPool threadPool;
    private TesseractMailTemplate mailTemplate;
    private EventBus mailEventBus;


    public ISchedulerThreadPool getThreadPool() {
        return threadPool;
    }

    public void dispatchTrigger(List<TesseractTrigger> triggerList, boolean isOnce) {
        triggerList.stream().forEach(trigger -> threadPool.runJob(new TaskRunnable(trigger, isOnce)));
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

        public TaskRunnable(TesseractTrigger trigger, boolean isOnce) {
            this.trigger = trigger;
            this.isOnce = isOnce;
        }

        @Override
        public void run() {
            try {
                //构建日志
                TesseractLog tesseractLog = new TesseractLog();
                tesseractLog.setClassName("");
                tesseractLog.setCreateTime(System.currentTimeMillis());
                tesseractLog.setCreator("test");
                tesseractLog.setGroupId(trigger.getGroupId());
                tesseractLog.setGroupName(trigger.getGroupName());
                tesseractLog.setTriggerName(trigger.getName());
                tesseractLog.setEndTime(0L);
                tesseractLog.setExecutorDetailId(0);
                //获取job detail
                QueryWrapper<TesseractJobDetail> jobQueryWrapper = new QueryWrapper<>();
                jobQueryWrapper.lambda().eq(TesseractJobDetail::getTriggerId, trigger.getId());
                TesseractJobDetail jobDetail = tesseractJobDetailService.getOne(jobQueryWrapper);
                if (jobDetail == null) {
                    tesseractLog.setStatus(LOG_FAIL);
                    tesseractLog.setMsg("没有发现可运行job");
                    tesseractLog.setSocket(NULL_SOCKET);
                    tesseractLog.setEndTime(System.currentTimeMillis());
                    log.info("tesseractLog:{}", tesseractLog);
                    tesseractLogService.save(tesseractLog);
                    TesseractGroup group = groupService.getById(tesseractLog.getGroupId());
                    sendMail(tesseractLog, group);
                    return;
                }
                tesseractLog.setClassName(jobDetail.getClassName());
                //获取执行器
                TesseractExecutor executor = executorService.getById(trigger.getExecutorId());
                if (executor == null) {
                    tesseractLog.setStatus(LOG_FAIL);
                    tesseractLog.setMsg("没有找到可用执行器");
                    tesseractLog.setSocket(NULL_SOCKET);
                    tesseractLog.setEndTime(System.currentTimeMillis());
                    tesseractLogService.save(tesseractLog);
                    log.info("tesseractLog:{}", tesseractLog);
                    TesseractGroup group = groupService.getById(tesseractLog.getGroupId());
                    sendMail(tesseractLog, group);
                    return;
                }
                QueryWrapper<TesseractExecutorDetail> executorDetailQueryWrapper = new QueryWrapper<>();
                List<TesseractExecutorDetail> executorDetailList = executorDetailService.list(executorDetailQueryWrapper);
                if (CollectionUtils.isEmpty(executorDetailList)) {
                    tesseractLog.setStatus(LOG_FAIL);
                    tesseractLog.setMsg("执行器下没有可用机器");
                    tesseractLog.setSocket(NULL_SOCKET);
                    tesseractLog.setEndTime(System.currentTimeMillis());
                    tesseractLogService.save(tesseractLog);
                    log.info("tesseractLog:{}", tesseractLog);
                    TesseractGroup group = groupService.getById(tesseractLog.getGroupId());
                    sendMail(tesseractLog, group);
                    return;
                }
                //todo 广播
                //路由发送执行
                routerExecute(tesseractLog, executorDetailList, jobDetail);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 根据路由策略，选择机器执行
         *
         * @param tesseractLog
         * @param executorDetailList
         * @param jobDetail
         */
        private void routerExecute(TesseractLog tesseractLog, List<TesseractExecutorDetail> executorDetailList, TesseractJobDetail jobDetail) {
            TesseractExecutorDetail executorDetail = SCHEDULE_ROUTER_MAP.getOrDefault(trigger.getStrategy(), new HashRouter()).routerExecutor(executorDetailList);
            //首先保存日志，获取到日志id，便于异步更新
            tesseractLog.setSocket(executorDetail.getSocket());
            tesseractLog.setMsg("执行中");
            tesseractLog.setStatus(AdminConstant.LOG_WAIT);
            tesseractLog.setExecutorDetailId(executorDetail.getId());
            tesseractLogService.save(tesseractLog);
            //将触发器加入fired_trigger
            TesseractFiredTrigger tesseractFiredTrigger = new TesseractFiredTrigger();
            tesseractFiredTrigger.setCreateTime(System.currentTimeMillis());
            tesseractFiredTrigger.setName(trigger.getName());
            tesseractFiredTrigger.setTriggerId(trigger.getId());
            tesseractFiredTrigger.setClassName(jobDetail.getClassName());
            tesseractFiredTrigger.setExecutorDetailId(executorDetail.getId());
            tesseractFiredTrigger.setSocket(executorDetail.getSocket());
            tesseractFiredTrigger.setLogId(tesseractLog.getId());
            firedTriggerService.save(tesseractFiredTrigger);
            //构建请求
            TesseractExecutorRequest executorRequest = new TesseractExecutorRequest();
            executorRequest.setClassName(jobDetail.getClassName());
            executorRequest.setShardingIndex(trigger.getShardingNum());
            executorRequest.setLogId(tesseractLog.getId());
            executorRequest.setTriggerId(trigger.getId());
            executorRequest.setExecutorId(executorDetail.getExecutorId());
            //发送调度请求
            TesseractExecutorResponse response = TesseractExecutorResponse.FAIL;
            log.info("开始调度:{}", executorRequest);
            try {
                response = feignService.sendToExecutor(new URI(HTTP_PREFIX + executorDetail.getSocket() + EXECUTE_MAPPING), executorRequest);
            } catch (URISyntaxException e) {
                log.error("URI异常:{}", e.getMessage());
                response = TesseractExecutorResponse.builder().body("URI异常").status(TesseractExecutorResponse.FAIL_STAUTS).build();
            }
            //执行成功直接返回等待执行后更新日志状态
            if (response.getStatus() == TesseractExecutorResponse.SUCCESS_STATUS) {
                return;
            }
            //如果执行失败则更新日志状态并且移出执行表
            tesseractLog.setStatus(AdminConstant.LOG_FAIL);
            tesseractLog.setEndTime(System.currentTimeMillis());
            Object body = response.getBody();
            if (body != null) {
                tesseractLog.setMsg(body.toString());
            }
            //移出执行表并修改日志状态
            firedTriggerService.removeFiredTriggerAndUpdateLog(trigger.getId(), executorDetail.getId(), tesseractLog);
            log.info("tesseractLog:{}", tesseractLog);
            //发送邮件
            TesseractGroup group = groupService.getById(tesseractLog.getGroupId());
            sendMail(tesseractLog, group);
        }

        /**
         * 失败后发送报警邮件
         */
        private void sendMail(TesseractLog tesseractLog, TesseractGroup group) {
            HashMap<String, Object> model = Maps.newHashMap();
            String body = mailTemplate.buildMailBody(LOG_TEMPLATE_NAME, model);
            MailEvent mailEvent = new MailEvent();
            mailEvent.setBody(body);
            mailEvent.setSubject(LOG_SUBJECT);
            mailEvent.setTo(group.getMail());
            mailEventBus.post(mailEvent);
        }

    }

    public void stop() {
        threadPool.shutdown();
    }
}