package admin.service.impl;

import admin.entity.*;
import admin.mapper.TesseractExecutorMapper;
import admin.pojo.VO.ExecutorVO;
import admin.pojo.VO.PageVO;
import admin.pojo.VO.TesseractExecutorVO;
import admin.security.SecurityUserContextHolder;
import admin.service.ITesseractExecutorDetailService;
import admin.service.ITesseractExecutorService;
import admin.service.ITesseractJobDetailService;
import admin.service.ITesseractTriggerService;
import admin.util.AdminUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tesseract.core.dto.TesseractAdminJobDetailDTO;
import tesseract.core.dto.TesseractAdminRegistryRequest;
import tesseract.core.dto.TesseractAdminRegistryResDTO;
import tesseract.exception.TesseractException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

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
@Transactional(rollbackFor = Exception.class)
public class TesseractExecutorServiceImpl extends ServiceImpl<TesseractExecutorMapper, TesseractExecutor> implements ITesseractExecutorService {
    @Autowired
    private ITesseractTriggerService triggerService;

    @Autowired
    private ITesseractExecutorService executorService;

    @Autowired
    private ITesseractExecutorDetailService executorDetailService;
    @Autowired
    private ITesseractJobDetailService jobDetailService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TesseractAdminRegistryResDTO registry(TesseractAdminRegistryRequest tesseractAdminRegistryRequest) throws Exception {
        @NotBlank String ip = tesseractAdminRegistryRequest.getIp();
        @NotNull Integer port = tesseractAdminRegistryRequest.getPort();
        String socket = ip + ":" + port;
        return toRegistry(socket, tesseractAdminRegistryRequest.getTesseractAdminJobDetailDTOList());
    }

    @Override
    public ExecutorVO listByPage(Long currentPage, Long pageSize, TesseractExecutor condition,
                                 Long startCreateTime,
                                 Long endCreateTime) {
        ExecutorVO executorVO = new ExecutorVO();
        Page<TesseractExecutor> tesseractExecutorPage = new Page<>(currentPage, pageSize);
        QueryWrapper<TesseractExecutor> queryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<TesseractExecutor> lambda = queryWrapper.lambda();
        //日期
        if (startCreateTime != null) {
            lambda.ge(TesseractExecutor::getCreateTime, startCreateTime);
        }

        if (endCreateTime != null) {
            lambda.le(TesseractExecutor::getCreateTime, endCreateTime);
        }

        //其他
        AdminUtils.buildCondition(queryWrapper, condition);
        lambda.orderByDesc(TesseractExecutor::getCreateTime);
        IPage<TesseractExecutor> page = page(tesseractExecutorPage, queryWrapper);
        PageVO pageVO = new PageVO();
        pageVO.setCurrentPage(currentPage);
        pageVO.setPageSize(pageSize);
        pageVO.setTotal(page.getTotal());
        List<TesseractExecutorVO> executorVOList = Collections.synchronizedList(Lists.newArrayList());
        List<TesseractExecutor> executorList = page.getRecords();
        executorList.parallelStream().forEach(executor -> {
            QueryWrapper<TesseractExecutorDetail> detailQueryWrapper = new QueryWrapper<>();
            detailQueryWrapper.lambda().eq(TesseractExecutorDetail::getExecutorId, executor.getId());
            List<TesseractExecutorDetail> executorDetailList = executorDetailService.list(detailQueryWrapper);
            TesseractExecutorVO tesseractExecutorVO = new TesseractExecutorVO();
            tesseractExecutorVO.setExecutor(executor);
            tesseractExecutorVO.setExecutorDetailList(executorDetailList);
            executorVOList.add(tesseractExecutorVO);
        });
        executorVO.setPageInfo(pageVO);
        executorVO.setExecutorList(executorVOList);
        return executorVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateExecutor(TesseractExecutor tesseractExecutor) {
        Integer executorId = tesseractExecutor.getId();
        //更新操作
        if (executorId != null) {
            TesseractExecutor executor = getById(executorId);
            if (executor == null) {
                throw new TesseractException("执行器为空");
            }
            if (!executor.getGroupName().equals(tesseractExecutor.getGroupName())) {
                //更新执行器下的触发器的所属组
                QueryWrapper<TesseractTrigger> triggerQueryWrapper = new QueryWrapper<>();
                List<TesseractTrigger> triggerList = triggerService.list(triggerQueryWrapper);
                @NotBlank String groupName = tesseractExecutor.getGroupName();
                @NotNull Integer groupId = tesseractExecutor.getGroupId();
                triggerList.parallelStream().forEach(trigger -> {
                    trigger.setGroupName(groupName);
                    trigger.setGroupId(groupId);
                });
                triggerService.updateBatchById(triggerList);
            }
            this.updateById(tesseractExecutor);
            return;
        }
        //新增操作
        doSave(tesseractExecutor);
    }

    private void doSave(TesseractExecutor tesseractExecutor) {
        QueryWrapper<TesseractExecutor> executorQueryWrapper = new QueryWrapper<>();
        executorQueryWrapper.lambda().eq(TesseractExecutor::getName, tesseractExecutor.getName());
        if (this.getOne(executorQueryWrapper) != null) {
            throw new TesseractException("执行器名称重复");
        }
        tesseractExecutor.setCreateTime(System.currentTimeMillis());
        tesseractExecutor.setCreator(SecurityUserContextHolder.getUser().getUsername());
        save(tesseractExecutor);
    }


    @Override
    public void deleteExecutor(Integer executorId) {
        QueryWrapper<TesseractTrigger> triggerQueryWrapper = new QueryWrapper<>();
        triggerQueryWrapper.lambda().eq(TesseractTrigger::getExecutorId, executorId);
        List<TesseractTrigger> triggerList = triggerService.list(triggerQueryWrapper);
        if (!CollectionUtils.isEmpty(triggerList)) {
            throw new TesseractException("执行器内还有触发器，不能删除");
        }
        removeById(executorId);
    }

    @Override
    public List<TesseractExecutor> executorListNoDetail(Integer groupId) {
        QueryWrapper<TesseractExecutor> executorQueryWrapper = new QueryWrapper<>();
        if (groupId != null) {
            executorQueryWrapper.lambda().eq(TesseractExecutor::getGroupId, groupId);
        }
        return this.list(executorQueryWrapper);
    }

    private TesseractAdminRegistryResDTO toRegistry(String socket, List<TesseractAdminJobDetailDTO> tesseractAdminJobDetailDTOList) {
        final List<String> repeatJobList = Collections.synchronizedList(Lists.newArrayList());
        List<TesseractJobDetail> jobDetailList = Collections.synchronizedList(Lists.newArrayList());
        List<String> noExecutorList = Collections.synchronizedList(Lists.newArrayList());
        List<String> noTriggerList = Collections.synchronizedList(Lists.newArrayList());
        //保存job
        tesseractAdminJobDetailDTOList.parallelStream().forEach(tesseractAdminJobDetailDTO -> {
                    @NotBlank String className = tesseractAdminJobDetailDTO.getClassName();
                    @NotBlank String triggerName = tesseractAdminJobDetailDTO.getTriggerName();
                    //检测触发器是否存在
                    QueryWrapper<TesseractTrigger> triggerQueryWrapper = new QueryWrapper<>();
                    triggerQueryWrapper.lambda().eq(TesseractTrigger::getName, triggerName);
                    TesseractTrigger trigger = triggerService.getOne(triggerQueryWrapper);
                    if (trigger == null) {
                        log.warn("触发器{}不存在", triggerName);
                        noTriggerList.add(triggerName);
                        return;
                    }
                    //检测执行器是否存在
                    @NotNull Integer executorId = trigger.getExecutorId();
                    TesseractExecutor executor = executorService.getById(executorId);
                    if (executor == null) {
                        log.warn("执行器{}不存在", executorId);
                        noExecutorList.add(executorId.toString());
                        return;
                    }

                    bindExecutor(executor, socket);
                    //以防任务重复注册，添加进job
                    QueryWrapper<TesseractJobDetail> jobDetailQueryWrapper = new QueryWrapper<>();
                    jobDetailQueryWrapper.lambda().eq(TesseractJobDetail::getTriggerId, trigger.getId()).eq(TesseractJobDetail::getClassName, className);
                    TesseractJobDetail jobDetail = jobDetailService.getOne(jobDetailQueryWrapper);
                    if (jobDetail != null) {
                        log.warn("重复任务{}", jobDetail);
                        repeatJobList.add(jobDetail.getClassName());
                        return;
                    }
                    jobDetail = new TesseractJobDetail();
                    jobDetail.setClassName(className);
                    jobDetail.setCreator(trigger.getCreator());
                    jobDetail.setTriggerId(trigger.getId());
                    jobDetail.setCreateTime(System.currentTimeMillis());
                    jobDetailList.add(jobDetail);
                }
        );
        // todo 由于多实例并发，这里插入任务需要加锁 目前占时采用 数据库唯一索引来保证不重复
        jobDetailService.saveBatch(jobDetailList);
        TesseractAdminRegistryResDTO tesseractAdminRegistryResDTO = new TesseractAdminRegistryResDTO();
        tesseractAdminRegistryResDTO.setNotTriggerNameList(noTriggerList);
        tesseractAdminRegistryResDTO.setNoExecutorList(noExecutorList);
        tesseractAdminRegistryResDTO.setRepeatJobList(repeatJobList);
        return tesseractAdminRegistryResDTO;
    }

    /**
     * 将机器和执行器绑定
     *
     * @param executor
     */
    private void bindExecutor(TesseractExecutor executor, String socket) {
        //防止重复插入
        Integer executorId = executor.getId();
        QueryWrapper<TesseractExecutorDetail> executorDetailQueryWrapper = new QueryWrapper<>();
        executorDetailQueryWrapper.lambda().eq(TesseractExecutorDetail::getSocket, socket);
        TesseractExecutorDetail executorDetail = executorDetailService.getOne(executorDetailQueryWrapper);
        if (executorDetail != null) {
            log.warn("机器{}已注册，将忽略关联", executorDetail);
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        executorDetail = new TesseractExecutorDetail();
        executorDetail.setGroupId(executor.getGroupId());
        executorDetail.setGroupName(executor.getGroupName());
        executorDetail.setExecutorId(executorId);
        executorDetail.setSocket(socket);
        executorDetail.setUpdateTime(currentTimeMillis);
        executorDetail.setCreateTime(currentTimeMillis);
        executorDetail.setLoadFactor(0D);
        executorDetailService.save(executorDetail);
    }
}
