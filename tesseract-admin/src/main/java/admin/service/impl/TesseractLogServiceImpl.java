package admin.service.impl;

import admin.core.component.SendMailComponent;
import admin.core.event.RetryEvent;
import admin.entity.TesseractFiredJob;
import admin.entity.TesseractLog;
import admin.entity.TesseractTrigger;
import admin.mapper.TesseractLogMapper;
import admin.pojo.StatisticsLogDO;
import admin.service.ITesseractFiredJobService;
import admin.service.ITesseractLogService;
import admin.service.ITesseractTriggerService;
import admin.util.AdminUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
    private SendMailComponent sendMailComponent;

    @Autowired
    private EventBus retryEventBus;

    @Autowired
    private ITesseractTriggerService tesseractTriggerService;

    private int statisticsDays = 7;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void notify(TesseractAdminJobNotify tesseractAdminJobNotify) {
        Long logId = tesseractAdminJobNotify.getLogId();
        String exception = tesseractAdminJobNotify.getException();
//        Integer jobId = tesseractAdminJobNotify.getJobId();
        TesseractLog tesseractLog = this.getById(logId);
        @NotNull Integer triggerId = tesseractAdminJobNotify.getTriggerId();
        TesseractTrigger tesseractTrigger = tesseractTriggerService.getById(triggerId);
        if (tesseractLog == null) {
            log.error("获取日志为空:{}", tesseractAdminJobNotify);
            throw new TesseractException("获取日志为空" + tesseractAdminJobNotify);
        }
        QueryWrapper<TesseractFiredJob> firedJobQueryWrapper = new QueryWrapper<>();
        firedJobQueryWrapper.lambda().eq(TesseractFiredJob::getTriggerId, tesseractAdminJobNotify.getTriggerId());
        if (!StringUtils.isEmpty(exception)) {
            sendMailComponent.missionFailedSendMail(tesseractAdminJobNotify);
            tesseractLog.setStatus(LOG_FAIL);
            tesseractLog.setMsg(exception);
            firedJobQueryWrapper.lambda().eq(TesseractFiredJob::getLogId, tesseractAdminJobNotify.getLogId());
            TesseractFiredJob tesseractFiredJob = firedJobService.getOne(firedJobQueryWrapper);
            if (tesseractTrigger.getRetryCount() > tesseractFiredJob.getRetryCount()) {
                tesseractFiredJob.setRetryCount(tesseractFiredJob.getRetryCount() + 1);
                firedJobService.updateById(tesseractFiredJob);
            } else {
                firedJobService.remove(firedJobQueryWrapper);
            }
            //执行失败重试
            retry(tesseractAdminJobNotify);
        } else {
            tesseractLog.setStatus(LOG_SUCCESS);
            tesseractLog.setMsg("执行成功");
            firedJobService.remove(firedJobQueryWrapper);
        }
        tesseractLog.setEndTime(System.currentTimeMillis());
        //更新日志状态
        this.updateById(tesseractLog);
    }

    /**
     * 失败重试
     * @param tesseractAdminJobNotify
     */
    private void retry(TesseractAdminJobNotify tesseractAdminJobNotify) {
        RetryEvent retryEvent = new RetryEvent(tesseractAdminJobNotify);
        retryEventBus.post(retryEvent);
//        applicationContext.publishEvent(new RetryEvent(tesseractAdminJobNotify));

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
        LocalDate now = LocalDate.now();
        long startTime = now.minus(6, ChronoUnit.DAYS).atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long endTime = now.plus(1, ChronoUnit.DAYS).atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        Date startDate = new Date();
        startDate.setTime(startTime);
        Date endDate = new Date();
        startDate.setTime(endTime);
        log.info("startTime:{},endTime:{}", startDate, endDate);
        List<StatisticsLogDO> failStatisticsLogDOList = this.getBaseMapper().statisticsFailLog(startTime, endTime);
        List<StatisticsLogDO> successStatisticsLogDOList = this.getBaseMapper().statisticsSuccessLogLine(startTime, endTime);
        Map<String, Collection<Integer>> map = Maps.newHashMap();
        Collection<Integer> failCountList = AdminUtils.buildStatisticsList(failStatisticsLogDOList, statisticsDays);
        Collection<Integer> successCountList = AdminUtils.buildStatisticsList(successStatisticsLogDOList, statisticsDays);
        map.put("success", successCountList);
        map.put("fail", failCountList);
        return map;
    }

    @Override
    public List<Map<String, Object>> statisticsLogPie() {
        List<Map<String, Object>> list = Lists.newArrayList();
        List<StatisticsLogDO> statisticsLogDOList = this.getBaseMapper().statisticsSuccessLogPie();
        statisticsLogDOList.forEach(statisticsLogDO -> {
            HashMap<String, Object> hashMap = Maps.newHashMap();
            hashMap.put("name", statisticsLogDO.getDataStr());
            hashMap.put("value", statisticsLogDO.getNum());
            list.add(hashMap);
        });
        return list;
    }
}
