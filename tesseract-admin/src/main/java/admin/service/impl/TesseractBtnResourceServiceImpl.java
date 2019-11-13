package admin.service.impl;

import admin.entity.TesseractBtnResource;
import admin.entity.TesseractRoleBtn;
import admin.entity.TesseractMenuResource;
import admin.mapper.TesseractBtnResourceMapper;
import admin.security.SecurityUserContextHolder;
import admin.security.SecurityUserDetail;
import admin.service.ITesseractBtnResourceService;
import admin.service.ITesseractRoleBtnService;
import admin.service.ITesseractMenuResourceService;
import admin.util.AdminUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tesseract.exception.TesseractException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author nickle
 * @since 2019-07-11
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TesseractBtnResourceServiceImpl extends ServiceImpl<TesseractBtnResourceMapper, TesseractBtnResource> implements ITesseractBtnResourceService {
    @Autowired
    private ITesseractRoleBtnService roleBtnService;
    @Autowired
    private ITesseractMenuResourceService menuResourceService;

    @Override
    public IPage<TesseractBtnResource> listByPage(Integer currentPage, Integer pageSize, TesseractBtnResource condition, Long startCreateTime, Long endCreateTime) {
        Page<TesseractBtnResource> page = new Page<>(currentPage, pageSize);
        QueryWrapper<TesseractBtnResource> queryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<TesseractBtnResource> lambda = queryWrapper.lambda();
        //日期
        if (startCreateTime != null) {
            lambda.ge(TesseractBtnResource::getCreateTime, startCreateTime);
        }

        if (endCreateTime != null) {
            lambda.le(TesseractBtnResource::getCreateTime, endCreateTime);
        }
        //其他
        AdminUtils.buildCondition(queryWrapper, condition);
        return page(page, queryWrapper);
    }

    @Override
    public void saveOrUpdateBtn(TesseractBtnResource btnResource) {
        long currentTimeMillis = System.currentTimeMillis();
        SecurityUserDetail user = SecurityUserContextHolder.getUser();
        Integer id = btnResource.getId();
        //更新
        if (id != null) {
            TesseractBtnResource tesseractBtnResource = getById(id);
            if (tesseractBtnResource == null) {
                throw new TesseractException("按钮不存在");
            }
            this.updateById(btnResource);
            return;
        }
        AdminUtils.buildNewEntityCommonFields(btnResource, currentTimeMillis, user);
        this.save(btnResource);
    }

    @Override
    public void deleteBtn(Integer btnId) {
        //检测角色按钮关联
        QueryWrapper<TesseractRoleBtn> menuBtnQueryWrapper = new QueryWrapper<>();
        menuBtnQueryWrapper.lambda().eq(TesseractRoleBtn::getBtnId, btnId);
        List<TesseractRoleBtn> roleBtnList = roleBtnService.list(menuBtnQueryWrapper);
        if (!CollectionUtils.isEmpty(roleBtnList)) {
            throw new TesseractException("还有角色与按钮关联，请解绑后再删除");
        }
        //删除按钮
        removeById(btnId);
    }
    @Override
    public Collection<TesseractBtnResource> btnListByMenuIdAndRoleId(Integer roleId, Integer menuId) {
        QueryWrapper<TesseractRoleBtn> btnResourceQueryWrapper = new QueryWrapper<>();
        btnResourceQueryWrapper.lambda().eq(TesseractRoleBtn::getRoleId, roleId).eq(TesseractRoleBtn::getMenuId, menuId);
        List<TesseractRoleBtn> roleBtnList = roleBtnService.list(btnResourceQueryWrapper);
        List<Integer> btnIdList = roleBtnList.stream().map(TesseractRoleBtn::getBtnId).collect(Collectors.toList());
        Collection<TesseractBtnResource> tesseractBtnResources = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(btnIdList)) {
            tesseractBtnResources = listByIds(btnIdList);
        }
        return tesseractBtnResources;
    }
}
