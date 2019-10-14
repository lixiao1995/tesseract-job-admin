package admin.service.impl;

import admin.constant.AdminConstant;
import admin.core.TesseractJobServiceDelegator;
import admin.entity.TesseractFiredJob;
import admin.entity.TesseractLog;
import admin.mapper.TesseractFiredJobMapper;
import admin.pojo.VO.FiredTriggerVO;
import admin.pojo.VO.PageVO;
import admin.security.SecurityUserContextHolder;
import admin.security.SecurityUserDetail;
import admin.service.ITesseractFiredJobService;
import admin.service.ITesseractLogService;
import admin.util.AdminUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tesseract.core.dto.TesseractStopTaskRequest;
import tesseract.core.netty.NettyClient;
import tesseract.core.serializer.ISerializerService;
import tesseract.core.util.HttpUtils;
import tesseract.exception.TesseractException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static admin.core.TesseractJobServiceDelegator.CHANNEL_MAP;
import static tesseract.core.constant.CommonConstant.HTTP_PREFIX;
import static tesseract.core.constant.CommonConstant.STOP_MAPPING;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author nickle
 * @since 2019-07-18
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class TesseractFiredJobServiceImpl extends ServiceImpl<TesseractFiredJobMapper, TesseractFiredJob> implements ITesseractFiredJobService {
    @Autowired
    private ITesseractLogService logService;

    @Override
    public FiredTriggerVO findFiredTrigger(Long currentPage, Long pageSize, TesseractFiredJob condition) {
        Page<TesseractFiredJob> page = new Page<>(currentPage, pageSize);
        QueryWrapper<TesseractFiredJob> firedJobQueryWrapper = new QueryWrapper<>();
        AdminUtils.buildCondition(firedJobQueryWrapper, condition);
        IPage<TesseractFiredJob> pageInfo = page(page, firedJobQueryWrapper);
        FiredTriggerVO firedTriggerVO = new FiredTriggerVO();
        PageVO pageVO = new PageVO();
        pageVO.setCurrentPage(pageInfo.getCurrent());
        pageVO.setPageSize(pageInfo.getSize());
        pageVO.setTotal(pageInfo.getTotal());
        firedTriggerVO.setPageInfo(pageVO);
        List<TesseractFiredJob> triggerList = pageInfo.getRecords();
        firedTriggerVO.setFiredTriggerList(triggerList);
        return firedTriggerVO;
    }

    /**
     * 1、删除fired job
     * 2、设置日志为失败状态
     * 3、通知执行器停止任务
     * 4、发送报警邮件
     *
     * @param firedTriggerId
     */
    @Override
    public void stopFiredJob(Integer firedTriggerId) throws Exception {
        log.info("停止firedTriggerId:{}", firedTriggerId);
        TesseractFiredJob firedJob = this.getById(firedTriggerId);
        if (firedJob == null) {
            throw new TesseractException("任务已执行完毕，请刷新后重试");
        }
        boolean isDelete = this.removeById(firedTriggerId);
        if (!isDelete) {
            throw new TesseractException("任务已执行完毕，请刷新后重试");
        }
        TesseractLog log = logService.getById(firedJob.getLogId());
        if (log == null) {
            throw new TesseractException("log 查询为空");
        }
        SecurityUserDetail user = SecurityUserContextHolder.getUser();
        log.setStatus(AdminConstant.LOG_FAIL);
        log.setMsg("用户:" + user.getUsername() + " 取消");
        logService.updateById(log);
        notifyExecutor(firedJob);
    }

    private void notifyExecutor(TesseractFiredJob firedJob) throws URISyntaxException, InterruptedException {
        NettyClient nettyClient = CHANNEL_MAP.get(firedJob.getSocket());
        if (nettyClient == null) {
            log.error("当前执行器已失效");
            return;
        }
        Channel activeChannel = nettyClient.getActiveChannel();
        ISerializerService serializerService = TesseractJobServiceDelegator.serializerService;
        TesseractStopTaskRequest tesseractStopTaskRequest = new TesseractStopTaskRequest();
        tesseractStopTaskRequest.setFireJobId(firedJob.getId());
        byte[] serialize = serializerService.serialize(tesseractStopTaskRequest);
        URI uri = new URI(HTTP_PREFIX + firedJob.getSocket() + STOP_MAPPING);
        FullHttpRequest httpRequest = HttpUtils.buildDefaultFullHttpRequest(uri, serialize);
        activeChannel.writeAndFlush(httpRequest).sync();
    }
}
