package admin.service.impl;

import admin.entity.TesseractRoleBtn;
import admin.entity.TesseractMenuResource;
import admin.entity.TesseractRoleResources;
import admin.mapper.TesseractMenuResourceMapper;
import admin.security.SecurityUserDetail;
import admin.security.SecurityUserContextHolder;
import admin.service.ITesseractBtnResourceService;
import admin.service.ITesseractRoleBtnService;
import admin.service.ITesseractMenuResourceService;
import admin.service.ITesseractRoleResourcesService;
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
import org.springframework.util.StringUtils;
import tesseract.exception.TesseractException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author nickle
 * @since 2019-07-10
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TesseractMenuResourceServiceImpl extends ServiceImpl<TesseractMenuResourceMapper, TesseractMenuResource> implements ITesseractMenuResourceService {
    @Autowired
    private ITesseractRoleResourcesService roleResourcesService;
    @Autowired
    private ITesseractRoleBtnService menuBtnService;
    @Autowired
    private ITesseractBtnResourceService btnResourceService;

    @Override
    public IPage<TesseractMenuResource> listByPage(Integer currentPage, Integer pageSize, TesseractMenuResource condition, Long startCreateTime, Long endCreateTime) {

        Page<TesseractMenuResource> page = new Page<>(currentPage, pageSize);
        QueryWrapper<TesseractMenuResource> queryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<TesseractMenuResource> lambda = queryWrapper.lambda();
        //日期
        if (startCreateTime != null) {
            lambda.ge(TesseractMenuResource::getCreateTime, startCreateTime);
        }

        if (endCreateTime != null) {
            lambda.le(TesseractMenuResource::getCreateTime, endCreateTime);
        }
        //其他
        AdminUtils.buildCondition(queryWrapper, condition);
        return page(page, queryWrapper);
    }

    @Override
    public void saveOrUpdateMenu(TesseractMenuResource tesseractMenuResource) {
        long currentTimeMillis = System.currentTimeMillis();
        if (tesseractMenuResource.getParentId() == null) {
            tesseractMenuResource.setParentId(0);
            tesseractMenuResource.setParentName("");
        }
        if (!new Integer(0).equals(tesseractMenuResource.getParentId()) && StringUtils.isEmpty(tesseractMenuResource.getParentName())) {
            throw new TesseractException("父菜单名字为空");
        }
        SecurityUserDetail user = SecurityUserContextHolder.getUser();
        Integer id = tesseractMenuResource.getId();
        if (id != null) {
            AdminUtils.buildUpdateEntityCommonFields(tesseractMenuResource, currentTimeMillis, user);
            this.updateById(tesseractMenuResource);
            return;
        }
        AdminUtils.buildNewEntityCommonFields(tesseractMenuResource, currentTimeMillis, user);
        this.save(tesseractMenuResource);

    }

    @Override
    public void deleteMenu(Integer menuId) {
        //删除角色菜单关联表
        QueryWrapper<TesseractRoleResources> roleResourcesQueryWrapper = new QueryWrapper<>();
        roleResourcesQueryWrapper.lambda().eq(TesseractRoleResources::getMenuId, menuId);
        roleResourcesService.remove(roleResourcesQueryWrapper);
        //删除菜单
        removeById(menuId);
    }
}
