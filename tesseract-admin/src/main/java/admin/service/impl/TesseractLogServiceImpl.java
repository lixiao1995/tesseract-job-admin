
package admin.service.impl;

import admin.core.component.TesseractMailSender;
import admin.core.scheduler.TaskExecutorDelegate;
import admin.core.scheduler.bean.CurrentTaskInfo;
import admin.core.scheduler.bean.TaskContextInfo;
import admin.entity.*;
import admin.mapper.TesseractLogMapper;
import admin.pojo.DO.StatisticsLogDO;
import admin.security.SecurityUserContextHolder;
import admin.security.SecurityUserDetail;
import admin.service.*;
import admin.util.AdminUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tesseract.core.dto.TesseractAdminJobNotify;
import tesseract.exception.TesseractException;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static admin.constant.AdminConstant.LOG_FAIL;
import static admin.constant.AdminConstant.LOG_SUCCESS;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author nickle
 * @since 2019-07-03
 */

@Slf4j
@Service
public class TesseractLogServiceImpl extends ServiceImpl<TesseractLogMapper, TesseractLog> implements ITesseractLogService {

    @Autowired
    private ITesseractFiredJobService firedJobService;

    @Autowired
    private TesseractMailSender tesseractMailSender;

    @Autowired
    private ITesseractJobDetailService jobDetailService;

    @Autowired
    private ITesseractTriggerService tesseractTriggerService;

    @Autowired
    private ITesseractExecutorDetailService executorDetailService;

    private int statisticsDays = 7;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void notify(TesseractAdminJobNotify tesseractAdminJobNotify) {
        Long logId = tesseractAdminJobNotify.getLogId();
        String exception = tesseractAdminJobNotify.getException();
        TesseractLog tesseractLog = this.getById(logId);
        if (tesseractLog == null) {
            log.error("获取日志为空:{}", tesseractAdminJobNotify);
            throw new TesseractException("获取日志为空" + tesseractAdminJobNotify);
        }
        if (!StringUtils.isEmpty(exception)) {
            tesseractMailSender.missionFailedSendMail(tesseractAdminJobNotify);
            tesseractLog.setStatus(LOG_FAIL);
            tesseractLog.setMsg(exception);
            retry(tesseractAdminJobNotify, tesseractLog);
        } else {
            tesseractLog.setStatus(LOG_SUCCESS);
            tesseractLog.setMsg("执行成功");
            firedJobService.removeById(tesseractAdminJobNotify.getFireJobId());
        }
        tesseractLog.setEndTime(System.currentTimeMillis());
        //更新日志状态
        this.updateById(tesseractLog);
    }

    /**
     * 执行重试逻辑
     */
    private void retry(TesseractAdminJobNotify tesseractAdminJobNotify, TesseractLog tesseractLog) {
        Integer fireJobId = tesseractAdminJobNotify.getFireJobId();
        if (fireJobId == null) {
            log.error("fire job id 为null,{}", tesseractAdminJobNotify);
            return;
        }
        TesseractFiredJob firedJob = firedJobService.getById(fireJobId);
        if (firedJob == null) {
            log.error("fire job 为null,{}", tesseractAdminJobNotify);
            return;
        }
        Integer triggerId = firedJob.getTriggerId();
        TesseractTrigger trigger = tesseractTriggerService.getById(triggerId);
        @NotNull Integer retryCount = trigger.getRetryCount();
        if (firedJob.getRetryCount() < retryCount) {
            //重试
            TaskContextInfo taskContextInfo = buildTaskContextInfo(tesseractAdminJobNotify, trigger);
            CurrentTaskInfo currentTaskInfo = buildCurrentTaskInfo(tesseractAdminJobNotify, taskContextInfo, firedJob, tesseractLog);
            TaskExecutorDelegate.retry(currentTaskInfo);
            return;
        }
        log.info("达到重试次数");
    }

    /**
     * 构建CurrentTaskInfo
     *
     * @return
     */
    public CurrentTaskInfo buildCurrentTaskInfo(TesseractAdminJobNotify tesseractAdminJobNotify
            , TaskContextInfo taskContextInfo, TesseractFiredJob firedJob, TesseractLog tesseractLog) {
        TesseractExecutorDetail executorDetail = executorDetailService.getById(tesseractAdminJobNotify.getJobDetailId());
        CurrentTaskInfo currentTaskInfo = new CurrentTaskInfo(taskContextInfo);
        currentTaskInfo.setCurrentExecutorDetail(executorDetail);
        currentTaskInfo.setFiredJob(firedJob);
        currentTaskInfo.setLog(tesseractLog);
        currentTaskInfo.setRetry(true);
        currentTaskInfo.setShardingIndex(tesseractAdminJobNotify.getShardingIndex());
        return currentTaskInfo;
    }

    /**
     * 构建task context info
     *
     * @param tesseractAdminJobNotify
     * @param trigger
     * @return
     */
    private TaskContextInfo buildTaskContextInfo(TesseractAdminJobNotify tesseractAdminJobNotify, TesseractTrigger trigger) {
        TesseractJobDetail jobDetail = jobDetailService.getById(tesseractAdminJobNotify.getJobDetailId());
        TaskContextInfo taskContextInfo = new TaskContextInfo();
        QueryWrapper<TesseractExecutorDetail> executorDetailQueryWrapper = new QueryWrapper<>();
        executorDetailQueryWrapper.lambda().eq(TesseractExecutorDetail::getExecutorId, trigger.getExecutorId());
        List<TesseractExecutorDetail> list = executorDetailService.list(executorDetailQueryWrapper);
        taskContextInfo.setExecutorDetailList(list);
        taskContextInfo.setJobDetail(jobDetail);
        taskContextInfo.setTrigger(trigger);
        return taskContextInfo;
    }


    @Override
    public IPage<TesseractLog> listByPage(Integer currentPage, Integer pageSize, TesseractLog condition,
                                          Long startCreateTime,
                                          Long endCreateTime,
                                          Long startUpdateTime,
                                          Long endUpdateTime) {
        Page<TesseractLog> page = new Page<>(currentPage, pageSize);
        QueryWrapper<TesseractLog> queryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<TesseractLog> lambda = queryWrapper.lambda();
        //日期
        if (startCreateTime != null) {
            lambda.ge(TesseractLog::getCreateTime, startCreateTime);
        }

        if (endCreateTime != null) {
            lambda.le(TesseractLog::getCreateTime, endCreateTime);
        }
        if (startUpdateTime != null) {
            lambda.ge(TesseractLog::getStatus, startUpdateTime);
        }
        if (endUpdateTime != null) {
            lambda.le(TesseractLog::getEndTime, endUpdateTime);
        }
        //其他
        AdminUtils.buildCondition(queryWrapper, condition);
        //按时间倒序
        lambda.orderByDesc(TesseractLog::getCreateTime);
        return page(page, queryWrapper);
    }

    @Override
    public Map<String, Collection<Integer>> statisticsLogLine() {
        SecurityUserDetail user = SecurityUserContextHolder.getUser();
        LocalDate now = LocalDate.now();
        long startTime = now.minus(6, ChronoUnit.DAYS).atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long endTime = now.plus(1, ChronoUnit.DAYS).atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        Date startDate = new Date();
        startDate.setTime(startTime);
        Date endDate = new Date();
        startDate.setTime(endTime);
        log.info("startTime:{},endTime:{}", startDate, endDate);
        List<StatisticsLogDO> failStatisticsLogDOList = this.getBaseMapper().statisticsFailLog(startTime, endTime, user.getGroupId());
        List<StatisticsLogDO> successStatisticsLogDOList = this.getBaseMapper().statisticsSuccessLogLine(startTime, endTime, user.getGroupId());
        Map<String, Collection<Integer>> map = Maps.newHashMap();
        Collection<Integer> failCountList = AdminUtils.buildStatisticsList(failStatisticsLogDOList, statisticsDays);
        Collection<Integer> successCountList = AdminUtils.buildStatisticsList(successStatisticsLogDOList, statisticsDays);
        map.put("success", successCountList);
        map.put("fail", failCountList);
        return map;
    }

    @Override
    public List<Map<String, Object>> statisticsLogPie() {
        SecurityUserDetail user = SecurityUserContextHolder.getUser();
        List<Map<String, Object>> list = Lists.newArrayList();
        List<StatisticsLogDO> statisticsLogDOList = this.getBaseMapper().statisticsSuccessLogPie(user.getGroupId());
        statisticsLogDOList.forEach(statisticsLogDO -> {
            HashMap<String, Object> hashMap = Maps.newHashMap();
            hashMap.put("name", statisticsLogDO.getDataStr());
            hashMap.put("value", statisticsLogDO.getNum());
            list.add(hashMap);
        });
        return list;
    }
}
