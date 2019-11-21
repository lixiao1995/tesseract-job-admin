
package admin.service.impl;

import admin.core.event.RetryEvent;
import admin.entity.TesseractFiredJob;
import admin.entity.TesseractLog;
import admin.mapper.TesseractLogMapper;
import admin.pojo.DO.StatisticsLogDO;
import admin.security.SecurityUserContextHolder;
import admin.security.SecurityUserDetail;
import admin.service.ITesseractFiredJobService;
import admin.service.ITesseractLogService;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tesseract.core.dto.TesseractAdminJobNotify;
import tesseract.exception.TesseractException;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static admin.constant.AdminConstant.*;
import static tesseract.core.util.CommonUtils.checkListItem;

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
    @Qualifier("retryEventBus")
    private EventBus retryEventBus;
    /**
     * 分析天数
     */
    private int statisticsDays = 7;
    /**
     * 角色属性名
     */
    private String roleFieldName = "roleName";

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void notify(TesseractAdminJobNotify tesseractAdminJobNotify) {
        Long logId = tesseractAdminJobNotify.getLogId();
        Integer fireJobId = tesseractAdminJobNotify.getFireJobId();
        String exception = tesseractAdminJobNotify.getException();
        TesseractLog tesseractLog = this.getById(logId);
        if (fireJobId == null) {
            log.error("fireJobId为空:{}", tesseractAdminJobNotify);
            throw new TesseractException("fireJobId为空");
        }
        if (tesseractLog == null) {
            log.error("获取日志为空:{}", tesseractAdminJobNotify);
            throw new TesseractException("获取日志为空");
        }
        TesseractFiredJob firedJob = firedJobService.getById(fireJobId);
        if (firedJob == null) {
            //这里可能由用户取消,只需要打印日志即可
            log.error("任务:{},用户已取消", tesseractAdminJobNotify);
            return;
        }
        if (!StringUtils.isEmpty(exception)) {
            tesseractLog.setStatus(LOG_FAIL);
            tesseractLog.setMsg(exception);
            RetryEvent retryEvent = new RetryEvent();
            retryEvent.setFireJobId(fireJobId);
            retryEventBus.post(retryEvent);
        } else {
            tesseractLog.setStatus(LOG_SUCCESS);
            tesseractLog.setMsg("执行成功");
            firedJobService.removeById(fireJobId);
        }
        tesseractLog.setEndTime(System.currentTimeMillis());
        //更新日志状态
        this.updateById(tesseractLog);
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
        Integer groupId = null;
        if (checkListItem(user.getRoleList(), roleFieldName, SUPER_ADMIN_ROLE_NAME)) {
            groupId = user.getGroupId();
        }
        List<StatisticsLogDO> failStatisticsLogDOList = this.getBaseMapper().statisticsFailLog(startTime, endTime, groupId);
        List<StatisticsLogDO> successStatisticsLogDOList = this.getBaseMapper().statisticsSuccessLogLine(startTime, endTime, groupId);
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
        Integer groupId = null;
        if (checkListItem(user.getRoleList(), roleFieldName, SUPER_ADMIN_ROLE_NAME)) {
            groupId = user.getGroupId();
        }
        List<StatisticsLogDO> statisticsLogDOList = this.getBaseMapper().statisticsSuccessLogPie(groupId);
        statisticsLogDOList.forEach(statisticsLogDO -> {
            HashMap<String, Object> hashMap = Maps.newHashMap();
            hashMap.put("status", statisticsLogDO.getStatus());
            hashMap.put("value", statisticsLogDO.getNum());
            list.add(hashMap);
        });
        return list;
    }
}
