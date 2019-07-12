package admin.core.scanner;

import admin.core.event.MailEvent;
import admin.core.mail.TesseractMailTemplate;
import admin.entity.TesseractExecutorDetail;
import admin.entity.TesseractFiredTrigger;
import admin.entity.TesseractLog;
import admin.service.ITesseractExecutorDetailService;
import admin.service.ITesseractFiredTriggerService;
import admin.service.ITesseractGroupService;
import admin.service.ITesseractLogService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import tesseract.core.lifecycle.IThreadLifycycle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static admin.constant.AdminConstant.LOG_FAIL;

/**
 * 失效执行器扫描线程
 *
 * @author nickle
 */
@Slf4j
public class ExecutorScanner extends Thread implements IThreadLifycycle {
    private static final String EXECUTOR_TEMPLATE_NAME = "executorTemplate.html";
    private static final String EXECUTOR_SUBJECT = "Tesseract-job 执行器报警邮件";
    private ITesseractExecutorDetailService executorDetailService;
    private ITesseractFiredTriggerService firedTriggerService;
    private ITesseractLogService logService;
    private ITesseractGroupService groupService;

    private volatile boolean isStop = false;
    private Long scanIntervalTime = 15 * 1000L;
    private TesseractMailTemplate mailTemplate;
    private EventBus mailEventBus;

    public ExecutorScanner(ITesseractLogService logService, ITesseractGroupService groupService, TesseractMailTemplate mailTemplate, EventBus mailEventBus, ITesseractExecutorDetailService executorDetailService) {
        super("ExecutorScanner");
        this.mailTemplate = mailTemplate;
        this.mailEventBus = mailEventBus;
        this.executorDetailService = executorDetailService;
        this.groupService = groupService;
        this.logService = logService;
    }

    /**
     * 扫描失去心跳的机器，如果失去心跳：
     * 1、删除executor_detail中数据
     * 2、修改正在等待执行的日志为失败
     * 3、发送报警邮件
     * 4、移出正在执行的trigger
     */
    @Override
    public void run() {
        log.info("ExecutorScanner start");
        while (!isStop) {
            try {
                List<TesseractExecutorDetail> tesseractExecutorDetails = executorDetailService.listInvalid();
                log.info("失效的机器:{}", tesseractExecutorDetails);
                if (!CollectionUtils.isEmpty(tesseractExecutorDetails)) {
                    List<Integer> detailIdList = Lists.newArrayList();
                    HashMap<Integer, List<TesseractExecutorDetail>> hashMap = Maps.newHashMap();
                    //按group拆开发送邮件
                    tesseractExecutorDetails.stream().forEach(executorDetail -> {
                        Integer groupId = executorDetail.getGroupId();
                        List<TesseractExecutorDetail> executorDetailList = hashMap.get(groupId);
                        if (executorDetailList == null) {
                            executorDetailList = Lists.newArrayList();
                            hashMap.put(groupId, executorDetailList);
                        }
                        executorDetailList.add(executorDetail);
                        detailIdList.add(executorDetail.getId());
                    });
                    //删除detail表中数据
                    executorDetailService.removeByIds(detailIdList);
                    //移出fired trigger
                    QueryWrapper<TesseractFiredTrigger> firedTriggerQueryWrapper = new QueryWrapper<>();
                    firedTriggerQueryWrapper.lambda().in(TesseractFiredTrigger::getExecutorDetailId, detailIdList);
                    firedTriggerService.remove(firedTriggerQueryWrapper);
                    //修改日志状态
                    modifyLogStatus(detailIdList);
                    //发送报警邮件
                    sendMail(hashMap);
                }
            } catch (Exception e) {
                log.error("发生异常:{}", e.getMessage());
            }
            try {
                Thread.sleep(scanIntervalTime);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * 修改日志状态
     */
    private void modifyLogStatus(List<Integer> detailIdList) {
        QueryWrapper<TesseractLog> logQueryWrapper = new QueryWrapper<>();
        logQueryWrapper.lambda().in(TesseractLog::getExecutorDetailId, detailIdList);
        TesseractLog log = new TesseractLog();
        log.setStatus(LOG_FAIL);
        logService.update(log, logQueryWrapper);
    }


    /**
     * 发送报警邮件
     *
     * @param executorDetailMap key：group id value：组下的detail
     */
    private void sendMail(Map<Integer, List<TesseractExecutorDetail>> executorDetailMap) {
        Set<Map.Entry<Integer, List<TesseractExecutorDetail>>> entries = executorDetailMap.entrySet();
        entries.parallelStream().forEach(entry -> {
            Integer groupId = entry.getKey();
            List<TesseractExecutorDetail> executorDetailList = entry.getValue();
            HashMap<String, Object> model = Maps.newHashMap();
            model.put("executorDetailList", executorDetailList);
            MailEvent mailEvent = new MailEvent();
            mailEvent.setBody(mailTemplate.buildMailBody(EXECUTOR_TEMPLATE_NAME, model));
            mailEvent.setSubject(EXECUTOR_SUBJECT);
            mailEvent.setTo(groupService.getById(groupId).getMail());
            mailEventBus.post(mailEvent);
        });

    }


    @Override
    public void initThread() {

    }

    @Override
    public void startThread() {
        this.start();
    }

    @Override
    public void stopThread() {
        this.isStop = true;
        this.interrupt();
    }
}
