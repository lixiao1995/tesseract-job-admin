package admin.service.impl;

import admin.entity.*;
import admin.mapper.TesseractRoleMapper;
import admin.pojo.DO.BtnDO;
import admin.pojo.DO.TesseractMenuDO;
import admin.pojo.DO.TesseractRoleDO;
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
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tesseract.exception.TesseractException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static admin.constant.AdminConstant.SUPER_ADMIN_NAME;
import static admin.constant.AdminConstant.SUPER_ADMIN_ROLE_NAME;

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
        //超级管理员不允许修改
        if (SUPER_ADMIN_ROLE_NAME.equals(tesseractRoleDO.getRoleName())) {
            throw new TesseractException("超级管理员角色不允许修改");
        }

        SecurityUserDetail user = SecurityUserContextHolder.getUser();
        Integer roleId = tesseractRoleDO.getRoleId();
        //更新
        if (roleId != null) {
            this.doUpdate(roleId, tesseractRoleDO, user);
            return;
        }
        this.doSave(tesseractRoleDO, user);
    }

    @Override
    public void deleteRole(Integer roleId) {
        //超级管理员不允许删除
        TesseractRole tesseractRole = getById(roleId);
        if (tesseractRole == null) {
            throw new TesseractException("角色为空");
        }
        if (SUPER_ADMIN_ROLE_NAME.equals(tesseractRole.getRoleName())) {
            throw new TesseractException("超级管理员角色不允许被删除");
        }
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

    /**
     * 更新角色表
     *
     * @param role
     */
    private void updateRole(TesseractRole role, TesseractRoleDO tesseractRoleDO, SecurityUserDetail user) {
        AdminUtils.buildUpdateEntityCommonFields(role, System.currentTimeMillis(), user);
        role.setRoleDesc(tesseractRoleDO.getRoleDesc());
        role.setRoleName(tesseractRoleDO.getRoleName());
        role.setUpdateTime(System.currentTimeMillis());
        this.updateById(role);
    }

    /**
     * 更新角色菜单
     *
     * @param role
     * @param roleResourcesList
     */
    private void updateRoleMenu(TesseractRole role, List<TesseractRoleResources> roleResourcesList) {
        Integer roleId = role.getId();
        //删除角色与菜单关联
        QueryWrapper<TesseractRoleResources> roleResourcesQueryWrapper = new QueryWrapper<>();
        roleResourcesQueryWrapper.lambda().eq(TesseractRoleResources::getRoleId, roleId);
        roleResourcesService.remove(roleResourcesQueryWrapper);

        if (!CollectionUtils.isEmpty(roleResourcesList)) {
            roleResourcesService.saveBatch(roleResourcesList);
        }
    }

    /**
     * 更新角色按钮
     *
     * @param role
     * @param roleBtnList
     */
    private void updateRoleBtn(TesseractRole role, List<TesseractRoleBtn> roleBtnList) {
        Integer roleId = role.getId();
        //删除角色与按钮关联
        QueryWrapper<TesseractRoleBtn> roleResourcesQueryWrapper = new QueryWrapper<>();
        roleResourcesQueryWrapper.lambda().eq(TesseractRoleBtn::getRoleId, roleId);
        roleBtnService.remove(roleResourcesQueryWrapper);

        if (!CollectionUtils.isEmpty(roleBtnList)) {
            roleBtnService.saveBatch(roleBtnList);
        }
    }

    /**
     * 更新操作
     *
     * @param roleId
     * @param tesseractRoleDO
     * @param user
     */
    private void doUpdate(Integer roleId, TesseractRoleDO tesseractRoleDO, SecurityUserDetail user) {
        TesseractRole role = getById(roleId);
        if (role == null) {
            throw new TesseractException("不存在的角色");
        }
        Map<String, List> map = buildRoleMap(tesseractRoleDO);
        List<TesseractRoleBtn> roleBtnList = map.get("roleBtnList");
        List<TesseractRoleResources> roleResourcesList = map.get("roleResourcesList");
        this.updateRole(role, tesseractRoleDO, user);
        this.updateRoleMenu(role, roleResourcesList);
        this.updateRoleBtn(role, roleBtnList);
    }

    /**
     * 保存操作
     *
     * @param tesseractRoleDO
     * @param user
     */
    private void doSave(TesseractRoleDO tesseractRoleDO, SecurityUserDetail user) {
        //新增
        TesseractRole role = new TesseractRole();
        role.setRoleName(tesseractRoleDO.getRoleName());
        role.setRoleDesc(tesseractRoleDO.getRoleDesc());
        AdminUtils.buildNewEntityCommonFields(role, System.currentTimeMillis(), user);
        this.save(role);
        //设置为新role的id
        tesseractRoleDO.setRoleId(role.getId());
        Map<String, List> map = buildRoleMap(tesseractRoleDO);
        List roleBtnList = map.get("roleBtnList");
        List roleResourcesList = map.get("roleResourcesList");
        if (!CollectionUtils.isEmpty(roleBtnList)) {
            roleBtnService.saveBatch(roleBtnList);
        }
        //保存菜单角色关联
        if (!CollectionUtils.isEmpty(roleResourcesList)) {
            roleResourcesService.saveBatch(roleResourcesList);
        }
    }

    /**
     * 解析出角色菜单，按钮
     *
     * @param tesseractRoleDO
     * @return
     */
    private Map<String, List> buildRoleMap(TesseractRoleDO tesseractRoleDO) {
        Map<String, List> map = Maps.newHashMap();
        Integer roleId = tesseractRoleDO.getRoleId();
        List<TesseractMenuDO> menuDOList = tesseractRoleDO.getMenuInfo();
        if (!CollectionUtils.isEmpty(menuDOList)) {
            //角色菜单关联
            List<TesseractRoleResources> roleResourcesList = Lists.newArrayList();
            //角色按钮关联
            List<TesseractRoleBtn> roleBtnList = Lists.newArrayList();
            menuDOList.stream().forEach(menuDO -> {
                List<BtnDO> btnList = menuDO.getBtnList();
                TesseractRoleResources tesseractMenuResource = new TesseractRoleResources();
                tesseractMenuResource.setMenuId(menuDO.getMenuId());
                tesseractMenuResource.setRoleId(roleId);
                if (btnList != null) {
                    btnList.forEach(btnDO -> {
                        TesseractRoleBtn tesseractRoleBtn = new TesseractRoleBtn();
                        tesseractRoleBtn.setBtnId(btnDO.getId());
                        tesseractRoleBtn.setRoleId(roleId);
                        tesseractRoleBtn.setMenuId(menuDO.getMenuId());
                        roleBtnList.add(tesseractRoleBtn);
                    });
                }
                roleResourcesList.add(tesseractMenuResource);
            });
            map.put("roleResourcesList", roleResourcesList);
            map.put("roleBtnList", roleBtnList);
        }
        return map;
    }


}
