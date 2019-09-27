package admin.service.impl;

import admin.core.mail.TesseractMailSender;
import admin.entity.TesseractExecutorDetail;
import admin.entity.TesseractGroup;
import admin.entity.TesseractLog;
import admin.mapper.TesseractExecutorDetailMapper;
import admin.service.ITesseractExecutorDetailService;
import admin.service.ITesseractFiredJobService;
import admin.service.ITesseractLockService;
import admin.service.ITesseractLogService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tesseract.core.dto.TesseractHeartbeatRequest;
import tesseract.exception.TesseractException;

import javax.validation.constraints.NotNull;
import java.util.List;

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
    private ITesseractFiredJobService firedJobService;
    @Autowired
    private ITesseractLogService logService;
    @Autowired
    private ITesseractLockService lockService;
    @Autowired
    private TesseractMailSender tesseractMailSender;

    @Override
    public void heartBeat(TesseractHeartbeatRequest heartBeatRequest) {
        @NotNull String socket = heartBeatRequest.getSocket();
        QueryWrapper<TesseractExecutorDetail> detailQueryWrapper = new QueryWrapper<>();
        detailQueryWrapper.lambda().eq(TesseractExecutorDetail::getSocket, socket);
        TesseractExecutorDetail executorDetail = getOne(detailQueryWrapper);
        if (executorDetail == null) {
            log.warn("机器:{}已失效", socket);
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
    public boolean clearInvalidMachine(TesseractGroup tesseractGroup, Integer pageSize, Long time) {
        lockService.lock(EXECUTOR_LOCK_NAME, tesseractGroup.getName());
        boolean flag = false;
        QueryWrapper<TesseractExecutorDetail> detailQueryWrapper = new QueryWrapper<>();
        detailQueryWrapper.lambda().le(TesseractExecutorDetail::getUpdateTime, time)
                .eq(TesseractExecutorDetail::getGroupId, tesseractGroup.getId());
        Page<TesseractExecutorDetail> page = new Page<>(1, pageSize);
        List<TesseractExecutorDetail> executorDetailList = page(page, detailQueryWrapper).getRecords();
        log.info("失效的机器:{}", executorDetailList);
        if (!CollectionUtils.isEmpty(executorDetailList)) {
            flag = true;
            List<Integer> detailIdList = Lists.newArrayList();
            executorDetailList.forEach(executorDetail -> detailIdList.add(executorDetail.getId()));
            //修改日志状态
            modifyLogStatus(detailIdList);
            //发送报警邮件
            tesseractMailSender.executorDetailListExceptionSendMail(tesseractGroup.getId(), executorDetailList);
            //暂时不执行重试，有可能机器还在执行任务,仅移除正在执行fired job
            this.removeByIds(detailIdList);
            removeFiredJob(detailIdList);
        }
        return flag;
    }

    /**
     * 修改日志状态
     */
    private void modifyLogStatus(List<Integer> detailIdList) {
        QueryWrapper<TesseractLog> logQueryWrapper = new QueryWrapper<>();
        logQueryWrapper.lambda().eq(TesseractLog::getStatus, LOG_WAIT)
                .in(TesseractLog::getExecutorDetailId, detailIdList);
        TesseractLog log = new TesseractLog();
        log.setStatus(LOG_FAIL);
        log.setMsg("机器失去心跳");
        logService.update(log, logQueryWrapper);
    }

    /**
     * 移除机器的fired job
     */
    private void removeFiredJob(List<Integer> detailIdList) {
        firedJobService.removeByIds(detailIdList);
    }

}
