package admin.service.impl;

import admin.core.component.SendMailComponent;
import admin.core.event.MailEvent;
import admin.core.mail.TesseractMailTemplate;
import admin.entity.TesseractExecutorDetail;
import admin.entity.TesseractFiredJob;
import admin.entity.TesseractLog;
import admin.mapper.TesseractExecutorDetailMapper;
import admin.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

import static admin.constant.AdminConstant.EXECUTOR_LOCK_NAME;
import static admin.constant.AdminConstant.LOG_FAIL;
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
    private SendMailComponent sendMailComponent;

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
            //移出fired job
            QueryWrapper<TesseractFiredJob> firedJobQueryWrapper = new QueryWrapper<>();
            firedJobQueryWrapper.lambda().in(TesseractFiredJob::getExecutorDetailId, detailIdList);
            firedJobService.remove(firedJobQueryWrapper);
            //修改日志状态
            modifyLogStatus(detailIdList);
            //发送报警邮件
//            sendMail(hashMap);
            sendMailComponent.executorDetailListExceptionSendMail(hashMap);

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

}
