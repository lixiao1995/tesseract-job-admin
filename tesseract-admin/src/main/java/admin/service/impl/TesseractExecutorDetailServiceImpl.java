package admin.service.impl;

import admin.core.event.MailEvent;
import admin.core.mail.TesseractMailTemplate;
import admin.entity.TesseractExecutorDetail;
import admin.entity.TesseractFiredTrigger;
import admin.entity.TesseractLog;
import admin.mapper.TesseractExecutorDetailMapper;
import admin.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tesseract.core.dto.TesseractHeartbeatRequest;
import tesseract.exception.TesseractException;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static admin.constant.AdminConstant.*;
import static tesseract.core.constant.CommonConstant.EXECUTOR_DETAIL_NOT_FIND;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author nickle
 * @since 2019-07-07
 */
@Service
@Slf4j
public class TesseractExecutorDetailServiceImpl extends ServiceImpl<TesseractExecutorDetailMapper, TesseractExecutorDetail> implements ITesseractExecutorDetailService {
    @Autowired
    private ITesseractFiredTriggerService firedTriggerService;
    @Autowired
    private ITesseractLogService logService;
    @Autowired
    private ITesseractLockService lockService;
    @Autowired
    private ITesseractGroupService groupService;
    @Autowired
    private TesseractMailTemplate mailTemplate;
    @Autowired
    private EventBus mailEventBus;
    private static final String EXECUTOR_TEMPLATE_NAME = "executorTemplate.html";
    private static final String EXECUTOR_SUBJECT = "Tesseract-job 执行器报警邮件";

    @Override
    public void heartBeat(TesseractHeartbeatRequest heartBeatRequest) {
        @NotNull String socket = heartBeatRequest.getSocket();
        QueryWrapper<TesseractExecutorDetail> detailQueryWrapper = new QueryWrapper<>();
        detailQueryWrapper.lambda().eq(TesseractExecutorDetail::getSocket, socket);
        TesseractExecutorDetail executorDetail = getOne(detailQueryWrapper);
        if (executorDetail == null) {
            log.warn("机器:{}已失效", socket);
            checkFiredTrigger(executorDetail);
            throw new TesseractException(EXECUTOR_DETAIL_NOT_FIND, "机器已失效");
        }
        executorDetail.setLoadFactor(caculateLoader(heartBeatRequest));
        executorDetail.setUpdateTime(System.currentTimeMillis());
        updateById(executorDetail);
    }

    private Double caculateLoader(TesseractHeartbeatRequest heartBeatRequest) {
        //目前先以阻塞队列大小来做负载
        return Double.valueOf(heartBeatRequest.getQueueSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean clearInvalidMachine(Integer pageSize, Long time) {
        lockService.lock(EXECUTOR_LOCK_NAME, EXECUTOR_LOCK_NAME);
        boolean flag = false;
        List<TesseractExecutorDetail> executorDetailList;
        QueryWrapper<TesseractExecutorDetail> detailQueryWrapper = new QueryWrapper<>();
        detailQueryWrapper.lambda().le(TesseractExecutorDetail::getUpdateTime, time);
        Page<TesseractExecutorDetail> page = new Page<>(1, pageSize);
        executorDetailList = page(page, detailQueryWrapper).getRecords();
        log.info("失效的机器:{}", executorDetailList);
        if (!CollectionUtils.isEmpty(executorDetailList)) {
            flag = true;
            //删除机器
            List<Integer> detailIdList = Lists.newArrayList();
            HashMap<Integer, List<TesseractExecutorDetail>> hashMap = Maps.newHashMap();
            //按group拆开发送邮件
            executorDetailList.stream().forEach(executorDetail -> {
                Integer groupId = executorDetail.getGroupId();
                List<TesseractExecutorDetail> tmpList = hashMap.get(groupId);
                if (tmpList == null) {
                    tmpList = Lists.newArrayList();
                    hashMap.put(groupId, tmpList);
                }
                tmpList.add(executorDetail);
                detailIdList.add(executorDetail.getId());
            });
            //删除detail表中数据
            this.removeByIds(detailIdList);
            //移出fired trigger
            QueryWrapper<TesseractFiredTrigger> firedTriggerQueryWrapper = new QueryWrapper<>();
            firedTriggerQueryWrapper.lambda().in(TesseractFiredTrigger::getExecutorDetailId, detailIdList);
            firedTriggerService.remove(firedTriggerQueryWrapper);
            //修改日志状态
            modifyLogStatus(detailIdList);
            //发送报警邮件
            sendMail(hashMap);

        }
        return flag;
    }

    /**
     * 修改日志状态
     */
    private void modifyLogStatus(List<Integer> detailIdList) {
        QueryWrapper<TesseractLog> logQueryWrapper = new QueryWrapper<>();
        logQueryWrapper.lambda().in(TesseractLog::getExecutorDetailId, detailIdList);
        TesseractLog log = new TesseractLog();
        log.setStatus(LOG_FAIL);
        log.setMsg("机器失去心跳");
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

    /**
     * 检查fired表，如果有则报警并且更改日志状态
     *
     * @param executorDetail
     */
    private void checkFiredTrigger(TesseractExecutorDetail executorDetail) {
        QueryWrapper<TesseractFiredTrigger> firedTriggerQueryWrapper = new QueryWrapper<>();
        firedTriggerQueryWrapper.lambda().eq(TesseractFiredTrigger::getExecutorDetailId, executorDetail.getId());
        List<TesseractFiredTrigger> firedTriggerList = firedTriggerService.list(firedTriggerQueryWrapper);
        if (!CollectionUtils.isEmpty(firedTriggerList)) {
            firedTriggerList.parallelStream().forEach(firedTrigger -> {
                // TODO: 2019/7/8  需要报警处理
                log.warn("滞留触发器列表:{}", firedTriggerList);
                Long logId = firedTrigger.getLogId();
                //更改日志状态
                TesseractLog log = new TesseractLog();
                log.setStatus(LOG_NO_CONFIRM);
                UpdateWrapper<TesseractLog> logUpdateWrapper = new UpdateWrapper<>();
                logUpdateWrapper.lambda().eq(TesseractLog::getId, logId);
                logService.update(log, logUpdateWrapper);
            });
        }
    }
}
