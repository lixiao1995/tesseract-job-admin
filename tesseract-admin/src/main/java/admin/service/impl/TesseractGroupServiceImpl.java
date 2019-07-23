package admin.service.impl;

import admin.core.scheduler.TesseractScheduleBoot;
import admin.entity.*;
import admin.mapper.TesseractGroupMapper;
import admin.security.SecurityUserContextHolder;
import admin.security.SecurityUserDetail;
import admin.service.ITesseractExecutorService;
import admin.service.ITesseractGroupService;
import admin.service.ITesseractTriggerService;
import admin.service.ITesseractUserService;
import admin.util.AdminUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tesseract.exception.TesseractException;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

import static admin.constant.AdminConstant.SUPER_ADMIN_NAME;
import static admin.util.AdminUtils.checkRoleAndCallback;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author nickle
 * @since 2019-07-10
 */
@Service
public class TesseractGroupServiceImpl extends ServiceImpl<TesseractGroupMapper, TesseractGroup> implements ITesseractGroupService {
    @Autowired
    private ITesseractTriggerService triggerService;

    @Autowired
    private ITesseractUserService userService;

    @Autowired
    private ITesseractExecutorService executorService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteGroup(Integer groupId) {
        TesseractGroup group = getById(groupId);
        if (group == null) {
            throw new TesseractException("组不存在");
        }
        //检测组下触发器，用户，执行器，如果有任何一个引用了这个组禁止删除
        //触发器
        QueryWrapper<TesseractTrigger> triggerQueryWrapper = new QueryWrapper<>();
        triggerQueryWrapper.lambda().eq(TesseractTrigger::getGroupId, groupId);
        List<TesseractTrigger> triggerList = triggerService.list(triggerQueryWrapper);
        if (!CollectionUtils.isEmpty(triggerList)) {
            throw new TesseractException("组内还有触发器，不能删除");
        }
        //用户
        QueryWrapper<TesseractUser> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.lambda().eq(TesseractUser::getGroupId, groupId);
        List<TesseractUser> userList = userService.list(userQueryWrapper);
        if (!CollectionUtils.isEmpty(userList)) {
            throw new TesseractException("组内还有用户，不能删除");
        }
        //执行器
        QueryWrapper<TesseractExecutor> executorQueryWrapper = new QueryWrapper<>();
        executorQueryWrapper.lambda().eq(TesseractExecutor::getGroupId, groupId);
        List<TesseractExecutor> executorList = executorService.list(executorQueryWrapper);
        if (!CollectionUtils.isEmpty(executorList)) {
            throw new TesseractException("组内还有执行器，不能删除");
        }
        //删除调度组
        removeById(groupId);
        //删除组调度器
        TesseractScheduleBoot.deleteGroupScheduler(group);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateGroup(TesseractGroup tesseractGroup) {
        long currentTimeMillis = System.currentTimeMillis();
        Integer id = tesseractGroup.getId();
        //更新操作
        if (id != null) {
            TesseractGroup oldGroup = getById(id);
            if (oldGroup == null) {
                throw new TesseractException("TesseractGroup为空");
            }
            Integer oldThreadPoolNum = oldGroup.getThreadPoolNum();
            Integer newThreadPoolNum = tesseractGroup.getThreadPoolNum();
            if (newThreadPoolNum <= 0) {
                throw new TesseractException("线程数不能为小于等于0");
            }
            if (!oldThreadPoolNum.equals(newThreadPoolNum)) {
                //更新线程数
                TesseractScheduleBoot.updateThreadNum(tesseractGroup.getName(), newThreadPoolNum);
            }
            tesseractGroup.setUpdateTime(currentTimeMillis);
            updateById(tesseractGroup);
            return;
        }
        //新增操作
        tesseractGroup.setCreator(SecurityUserContextHolder.getUser().getUsername());
        tesseractGroup.setCreateTime(currentTimeMillis);
        tesseractGroup.setUpdateTime(currentTimeMillis);
        this.save(tesseractGroup);
        //新增组调度器
        TesseractScheduleBoot.addGroupScheduler(tesseractGroup);
    }

    @Override
    public IPage<TesseractGroup> listByPage(Integer currentPage, Integer pageSize, TesseractGroup condition, Long startCreateTime, Long endCreateTime) {
        Page<TesseractGroup> page = new Page<>(currentPage, pageSize);
        QueryWrapper<TesseractGroup> queryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<TesseractGroup> lambda = queryWrapper.lambda();
        //日期
        if (startCreateTime != null) {
            lambda.ge(TesseractGroup::getCreateTime, startCreateTime);
        }

        if (endCreateTime != null) {
            lambda.le(TesseractGroup::getCreateTime, endCreateTime);
        }
        checkRoleAndCallback(groupId -> lambda.eq(TesseractGroup::getId, groupId));
        //其他
        AdminUtils.buildCondition(queryWrapper, condition);
        return page(page, queryWrapper);
    }

    @Override
    public List<TesseractGroup> allGroup() {
        QueryWrapper<TesseractGroup> groupQueryWrapper = new QueryWrapper<>();
        checkRoleAndCallback(groupId -> groupQueryWrapper.lambda().eq(TesseractGroup::getId, groupId));
        return this.list(groupQueryWrapper);
    }
}
