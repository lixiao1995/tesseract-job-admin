package admin.service.impl;

import admin.core.event.MailEvent;
import admin.core.mail.TesseractMailTemplate;
import admin.core.scheduler.CronExpression;
import admin.core.scheduler.TesseractScheduleBoot;
import admin.entity.TesseractExecutor;
import admin.entity.TesseractGroup;
import admin.entity.TesseractTrigger;
import admin.mapper.TesseractTriggerMapper;
import admin.pojo.VO.PageVO;
import admin.pojo.VO.TriggerVO;
import admin.security.SecurityUserContextHolder;
import admin.service.ITesseractExecutorService;
import admin.service.ITesseractGroupService;
import admin.service.ITesseractLockService;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tesseract.exception.TesseractException;

import javax.validation.constraints.NotBlank;
import java.text.ParseException;
import java.util.*;

import static admin.constant.AdminConstant.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author nickle
 * @since 2019-07-03
 */
@Service
@Slf4j
public class TesseractTriggerServiceImpl extends ServiceImpl<TesseractTriggerMapper, TesseractTrigger> implements ITesseractTriggerService {
    @Autowired
    private ITesseractLockService lockService;

    @Autowired
    private ITesseractExecutorService executorService;
    @Autowired
    private ITesseractGroupService groupService;

    @Autowired
    private EventBus mailEventBus;

    @Autowired
    private TesseractMailTemplate mailTemplate;
    /**
     * 常量
     */
    private static final String MISSFIRE_TEMPLATE_NAME = "missfireTemplate.html";
    private static final String MISSFIRE_SUBJECT = "Tesseract-job  missfire报警邮件";

    /**
     * 获取锁并获取到时间点之前的触发器
     *
     * @param batchSize
     * @param time
     * @param timeWindowSize
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<TesseractTrigger> findTriggerWithLock(TesseractGroup tesseractGroup, int batchSize, long time, Integer timeWindowSize) {
        lockService.lock(TRIGGER_LOCK_NAME, tesseractGroup.getName());
        QueryWrapper<TesseractTrigger> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .le(TesseractTrigger::getNextTriggerTime, time + timeWindowSize)
                .eq(TesseractTrigger::getStatus, TRGGER_STATUS_STARTING)
                .eq(TesseractTrigger::getGroupId, tesseractGroup.getId())
                .orderByDesc(TesseractTrigger::getNextTriggerTime);
        Page<TesseractTrigger> page = new Page<>(1, batchSize);
        IPage<TesseractTrigger> listPage = page(page, queryWrapper);
        List<TesseractTrigger> triggerList = listPage.getRecords();
        List<TesseractTrigger> updateTriggerList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(triggerList)) {
            triggerList.parallelStream().forEach(trigger -> {
                //构建cron计算器
                CronExpression cronExpression;
                try {
                    cronExpression = new CronExpression(trigger.getCron());
                } catch (ParseException e) {
                    log.error("创建CronExpression出错:{},异常信息:{}", trigger, e.getMessage());
                    return;
                }
                TesseractTrigger updateTrigger = new TesseractTrigger();
                updateTrigger.setId(trigger.getId());
                updateTrigger.setNextTriggerTime(cronExpression.getTimeAfter(new Date()).getTime());
                updateTrigger.setPrevTriggerTime(System.currentTimeMillis());
                updateTriggerList.add(updateTrigger);
            });
            log.info("下一次执行时间{}", new Date(updateTriggerList.get(0).getNextTriggerTime()));
            this.updateBatchById(updateTriggerList);
        }
        return triggerList;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrUpdateTrigger(TesseractTrigger tesseractTrigger) throws Exception {
        Integer triggerId = tesseractTrigger.getId();
        TesseractExecutor executor = executorService.getById(tesseractTrigger.getExecutorId());
        //更新
        if (triggerId != null) {
            TesseractTrigger trigger = getById(triggerId);
            @NotBlank String oldCron = trigger.getCron();
            //重新计算下一次调度时间
            if (!tesseractTrigger.getCron().equals(oldCron)) {
                tesseractTrigger.setNextTriggerTime(AdminUtils.caculateNextTime(tesseractTrigger.getCron()));
            }
            //如果更新了执行器则更新组名
            if (!trigger.getExecutorId().equals(tesseractTrigger.getExecutorId())) {
                tesseractTrigger.setGroupId(executor.getGroupId());
                tesseractTrigger.setGroupName(executor.getGroupName());
            }
            updateById(tesseractTrigger);
            return;
        }
        //新增
        long currentTimeMillis = System.currentTimeMillis();
        tesseractTrigger.setGroupId(executor.getGroupId());
        tesseractTrigger.setGroupName(executor.getGroupName());
        tesseractTrigger.setPrevTriggerTime(0L);
        tesseractTrigger.setCreator(SecurityUserContextHolder.getUser().getUsername());
        tesseractTrigger.setNextTriggerTime(AdminUtils.caculateNextTime(tesseractTrigger.getCron()));
        tesseractTrigger.setCreateTime(currentTimeMillis);
        if (StringUtils.isEmpty(tesseractTrigger.getExecuteParam())) {
            tesseractTrigger.setExecuteParam("");
        }
        tesseractTrigger.setStatus(TRGGER_STATUS_STOPING);
        tesseractTrigger.setUpdateTime(currentTimeMillis);
        this.save(tesseractTrigger);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resovleMissfireTrigger(TesseractGroup tesseractGroup, Integer pageSize, Long time) {
        lockService.lock(TRIGGER_LOCK_NAME, tesseractGroup.getName());
        boolean flag = false;
        QueryWrapper<TesseractTrigger> triggerQueryWrapper = new QueryWrapper<>();
        triggerQueryWrapper.lambda()
                .eq(TesseractTrigger::getStatus, TRGGER_STATUS_STARTING)
                .eq(TesseractTrigger::getGroupId, tesseractGroup.getId())
                //下次触发时间小于等于当前时间减掉校验时间
                .le(TesseractTrigger::getNextTriggerTime, time);
        Page<TesseractTrigger> triggerPage = new Page<>(1, pageSize);
        IPage<TesseractTrigger> page = page(triggerPage, triggerQueryWrapper);
        List<TesseractTrigger> triggerList = page.getRecords();
        log.info("missfire trigger:{}", triggerList);
        if (!CollectionUtils.isEmpty(triggerList)) {
            flag = true;
            //按组分类后发送邮件，避免发送多个邮件
            Map<Integer, List<TesseractTrigger>> map = new HashMap<>(16);
            triggerList.forEach(trigger -> {
                Integer groupId = trigger.getGroupId();
                List<TesseractTrigger> tmpTriggerList = map.get(groupId);
                if (tmpTriggerList == null) {
                    tmpTriggerList = Lists.newArrayList();
                    map.put(groupId, tmpTriggerList);
                }
                tmpTriggerList.add(trigger);
                //更新触发器调度时间为当前时间
                trigger.setNextTriggerTime(System.currentTimeMillis());
            });
            log.info("missfire 触发器:{}" + triggerList);
            //发送邮件
            map.entrySet().parallelStream().forEach(entry -> {
                Integer groupId = entry.getKey();
                List<TesseractTrigger> mailTriggerList = entry.getValue();
                MailEvent mailEvent = buildMailEvent(mailTriggerList, groupId);
                if (mailEvent != null) {
                    mailEventBus.post(mailEvent);
                }
            });
            this.updateBatchById(triggerList);
        }
        return flag;
    }

    /**
     * 构建邮件事件
     *
     * @param triggerList
     * @return
     */
    private MailEvent buildMailEvent(List<TesseractTrigger> triggerList, Integer groupId) {
        MailEvent mailEvent = new MailEvent();
        try {
            TesseractGroup tesseractGroup = groupService.getById(groupId);
            if (tesseractGroup == null) {
                throw new TesseractException("没有找到组信息，将无法发送邮件。组id:" + groupId);
            }
            HashMap<String, Object> model = Maps.newHashMap();
            model.put("triggerList", triggerList);
            model.put("groupName", tesseractGroup.getName());
            String body = mailTemplate.buildMailBody(MISSFIRE_TEMPLATE_NAME, model);
            mailEvent.setBody(body);
            mailEvent.setSubject(MISSFIRE_SUBJECT);
            mailEvent.setTo(tesseractGroup.getMail());
        } catch (Exception e) {
            log.error("构建邮件事件异常将无法发送邮件:{}", e.getMessage());
            return null;
        }
        return mailEvent;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TriggerVO listByPage(Integer currentPage, Integer pageSize, TesseractTrigger condition,
                                Long startCreateTime,
                                Long endCreateTime) {
        Page<TesseractTrigger> page = new Page<>(currentPage, pageSize);
        QueryWrapper<TesseractTrigger> queryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<TesseractTrigger> lambda = queryWrapper.lambda();
        //日期
        if (startCreateTime != null) {
            lambda.ge(TesseractTrigger::getCreateTime, startCreateTime);
        }

        if (endCreateTime != null) {
            lambda.le(TesseractTrigger::getCreateTime, endCreateTime);
        }
        AdminUtils.buildCondition(queryWrapper, condition);
        lambda.orderByDesc(TesseractTrigger::getCreateTime);
        IPage<TesseractTrigger> pageInfo = page(page, queryWrapper);
        TriggerVO triggerVO = new TriggerVO();
        PageVO pageVO = new PageVO();
        pageVO.setCurrentPage(pageInfo.getCurrent());
        pageVO.setPageSize(pageInfo.getSize());
        pageVO.setTotal(pageInfo.getTotal());
        triggerVO.setPageInfo(pageVO);
        List<TesseractTrigger> triggerList = pageInfo.getRecords();
        triggerVO.setTriggerList(triggerList);
        return triggerVO;
    }

    @Override
    public void executeTrigger(Integer groupId, Integer triggerId) {
        TesseractGroup group = groupService.getById(groupId);
        if (group == null) {
            throw new TesseractException(String.format("组为空,id: %s", groupId));
        }
        TesseractScheduleBoot.executeTrigger(group, Arrays.asList(getTriggerById(triggerId)));
    }

    @Override
    public void startTrigger(Integer triggerId) throws ParseException {
        TesseractTrigger trigger = getTriggerById(triggerId);
        if (StringUtils.isEmpty(trigger.getGroupName())) {
            throw new TesseractException("请先给触发器所属执行器添加组");
        }
        CronExpression cronExpression = new CronExpression(trigger.getCron());
        trigger.setNextTriggerTime(cronExpression.getTimeAfter(new Date()).getTime());
        trigger.setStatus(TRGGER_STATUS_STARTING);
        updateById(trigger);
    }

    @Override
    public void stopTrigger(Integer triggerId) {
        TesseractTrigger trigger = getTriggerById(triggerId);
        trigger.setStatus(TRGGER_STATUS_STOPING);
        updateById(trigger);
    }

    @Override
    public void deleteTrigger(Integer triggerId) {
        this.removeById(triggerId);
    }

    private TesseractTrigger getTriggerById(Integer triggerId) {
        TesseractTrigger trigger = getById(triggerId);
        if (trigger == null) {
            log.error("找不到对应触发器:{}", triggerId);
            throw new TesseractException("找不到对应触发器:" + triggerId);
        }
        return trigger;
    }
}
