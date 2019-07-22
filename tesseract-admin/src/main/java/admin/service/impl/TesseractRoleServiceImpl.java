package admin.service.impl;

import admin.entity.*;
import admin.mapper.TesseractRoleMapper;
import admin.pojo.BtnDO;
import admin.pojo.TesseractMenuDO;
import admin.pojo.TesseractRoleDO;
import admin.security.SecurityUserDetail;
import admin.security.SecurityUserContextHolder;
import admin.service.ITesseractRoleBtnService;
import admin.service.ITesseractRoleResourcesService;
import admin.service.ITesseractRoleService;
import admin.service.ITesseractUserRoleService;
import admin.util.AdminUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tesseract.exception.TesseractException;

import java.util.ArrayList;
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
public class TesseractRoleServiceImpl extends ServiceImpl<TesseractRoleMapper, TesseractRole> implements ITesseractRoleService {
    @Autowired
    private ITesseractRoleResourcesService roleResourcesService;
    @Autowired
    private ITesseractUserRoleService userRoleService;

    @Autowired
    private ITesseractRoleService roleService;
    @Autowired
    private ITesseractRoleBtnService roleBtnService;

    @Override
    public IPage<TesseractRole> listByPage(Integer currentPage, Integer pageSize, TesseractRole condition, Long startCreateTime, Long endCreateTime) {
        Page<TesseractRole> page = new Page<>(currentPage, pageSize);
        QueryWrapper<TesseractRole> queryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<TesseractRole> lambda = queryWrapper.lambda();
        //日期
        if (startCreateTime != null) {
            lambda.ge(TesseractRole::getCreateTime, startCreateTime);
        }

        if (endCreateTime != null) {
            lambda.le(TesseractRole::getCreateTime, endCreateTime);
        }
        //其他
        AdminUtils.buildCondition(queryWrapper, condition);
        return page(page, queryWrapper);
    }

    @Override
    public void saveOrUpdateRole(TesseractRoleDO tesseractRoleDO) {
        long currentTimeMillis = System.currentTimeMillis();
        SecurityUserDetail user = SecurityUserContextHolder.getUser();
        Integer roleId = tesseractRoleDO.getRoleId();
        //更新
        if (roleId != null) {
            TesseractRole role = getById(roleId);
            if (role == null) {
                throw new TesseractException("不存在的角色");
            }
            AdminUtils.buildUpdateEntityCommonFields(role, currentTimeMillis, user);
            //更新角色表
            this.updateById(role);
            //先删除角色菜单关联表
            QueryWrapper<TesseractRoleResources> roleResourcesQueryWrapper = new QueryWrapper<>();
            roleResourcesQueryWrapper.lambda().eq(TesseractRoleResources::getRoleId, roleId);
            roleResourcesService.remove(roleResourcesQueryWrapper);
            //删除角色按钮关联表
            QueryWrapper<TesseractRoleBtn> roleBtnQueryWrapper = new QueryWrapper<>();
            roleBtnQueryWrapper.lambda().eq(TesseractRoleBtn::getRoleId, roleId);
            roleBtnService.remove(roleBtnQueryWrapper);
            //新值插入表中
            List<TesseractMenuDO> menuDOList = tesseractRoleDO.getMenuInfo();
            if (!CollectionUtils.isEmpty(menuDOList)) {
                //插入新的角色菜单关联
                List<TesseractRoleResources> roleResourcesList = Lists.newArrayList();
                //角色按钮关联
                List<TesseractRoleBtn> roleBtnList = Lists.newArrayList();
                menuDOList.stream().forEach(menuDO -> {
                    List<BtnDO> btnList = menuDO.getBtnList();
                    TesseractRoleResources tesseractMenuResource = new TesseractRoleResources();
                    tesseractMenuResource.setMenuId(menuDO.getMenuId());
                    tesseractMenuResource.setRoleId(roleId);
                    btnList.forEach(btnDO -> {
                        TesseractRoleBtn tesseractRoleBtn = new TesseractRoleBtn();
                        tesseractRoleBtn.setBtnId(btnDO.getId());
                        tesseractRoleBtn.setRoleId(roleId);
                        tesseractRoleBtn.setMenuId(menuDO.getMenuId());
                        roleBtnList.add(tesseractRoleBtn);
                    });
                    roleResourcesList.add(tesseractMenuResource);
                });
                roleResourcesService.saveBatch(roleResourcesList);
                roleBtnService.saveBatch(roleBtnList);
            }
            return;
        }
        //新增
        TesseractRole role = new TesseractRole();
        role.setRoleName(tesseractRoleDO.getRoleName());
        role.setRoleDesc("123");
        AdminUtils.buildNewEntityCommonFields(role, currentTimeMillis, user);
        this.save(role);
        //保存菜单关联,角色按钮关联
        List<TesseractMenuDO> menuDOList = tesseractRoleDO.getMenuInfo();
        if (!CollectionUtils.isEmpty(menuDOList)) {
            //插入新的角色菜单关联
            List<TesseractRoleResources> roleResourcesList = Lists.newArrayList();
            //角色按钮关联
            List<TesseractRoleBtn> roleBtnList = Lists.newArrayList();
            menuDOList.stream().forEach(menuDO -> {
                List<BtnDO> btnList = menuDO.getBtnList();
                TesseractRoleResources tesseractMenuResource = new TesseractRoleResources();
                tesseractMenuResource.setMenuId(menuDO.getMenuId());
                tesseractMenuResource.setRoleId(role.getId());
                btnList.forEach(btnDO -> {
                    TesseractRoleBtn tesseractRoleBtn = new TesseractRoleBtn();
                    tesseractRoleBtn.setBtnId(btnDO.getId());
                    tesseractRoleBtn.setRoleId(role.getId());
                    tesseractRoleBtn.setMenuId(menuDO.getMenuId());
                    roleBtnList.add(tesseractRoleBtn);
                });
                roleResourcesList.add(tesseractMenuResource);
            });
            roleResourcesService.saveBatch(roleResourcesList);
            roleBtnService.saveBatch(roleBtnList);
        }
    }

    @Override
    public void deleteRole(Integer roleId) {
        //删除角色菜单关联
        QueryWrapper<TesseractRoleResources> roleResourcesQueryWrapper = new QueryWrapper<>();
        roleResourcesQueryWrapper.lambda().eq(TesseractRoleResources::getRoleId, roleId);
        roleResourcesService.remove(roleResourcesQueryWrapper);
        //删除角色用户关联
        QueryWrapper<TesseractUserRole> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper.lambda().eq(TesseractUserRole::getRoleId, roleId);
        userRoleService.remove(userRoleQueryWrapper);
        //删除角色
        this.removeById(roleId);
    }

    @Override
    public List<Integer> getRoleMenuIdList(Integer roleId) {
        QueryWrapper<TesseractRoleResources> roleResourcesQueryWrapper = new QueryWrapper<>();
        roleResourcesQueryWrapper.lambda().eq(TesseractRoleResources::getRoleId, roleId);
        return roleResourcesService.list(roleResourcesQueryWrapper).stream().map(TesseractRoleResources::getMenuId).collect(Collectors.toList());
    }

    @Override
    public List<TesseractRole> getRoleByUserId(Integer userId) {
        QueryWrapper<TesseractUserRole> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper.lambda().eq(TesseractUserRole::getUserId, userId);
        List<Integer> roleIdList = userRoleService.list(userRoleQueryWrapper).stream().map(TesseractUserRole::getRoleId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(roleIdList)) {
            return null;
        }
        QueryWrapper<TesseractRole> roleQueryWrapper = new QueryWrapper<>();
        roleQueryWrapper.lambda().in(TesseractRole::getId, roleIdList);
        return roleService.list(roleQueryWrapper);
    }
}
