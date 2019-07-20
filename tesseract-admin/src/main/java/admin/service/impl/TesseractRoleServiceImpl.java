package admin.service.impl;

import admin.entity.TesseractMenuResource;
import admin.entity.TesseractRole;
import admin.entity.TesseractRoleResources;
import admin.entity.TesseractUserRole;
import admin.mapper.TesseractRoleMapper;
import admin.pojo.TesseractMenuDO;
import admin.pojo.TesseractRoleDO;
import admin.security.SecurityUserDetail;
import admin.security.SecurityUserContextHolder;
import admin.service.ITesseractRoleResourcesService;
import admin.service.ITesseractRoleService;
import admin.service.ITesseractUserRoleService;
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
            /**
             * 修改后更新
             */
            if (!role.getRoleName().equals(tesseractRoleDO.getRoleName())) {
                this.updateById(role);
            }
            //更新角色菜单关联表
            //先删除关联表再插入新的
            QueryWrapper<TesseractRoleResources> roleResourcesQueryWrapper = new QueryWrapper<>();
            roleResourcesQueryWrapper.lambda().eq(TesseractRoleResources::getRoleId, roleId);
            roleResourcesService.remove(roleResourcesQueryWrapper);
            //插入新的值
            List<TesseractMenuDO> menuDOList = tesseractRoleDO.getMenuInfo();
            List<TesseractRoleResources> tesseractRoleResourcesList = menuDOList.stream().map(menuDO -> {
                TesseractRoleResources tesseractMenuResource = new TesseractRoleResources();
                tesseractMenuResource.setMenuId(menuDO.getMenuId());
                tesseractMenuResource.setRoleId(roleId);
                return tesseractMenuResource;
            }).collect(Collectors.toList());
            roleResourcesService.saveBatch(tesseractRoleResourcesList);
            return;
        }
        //新增
        TesseractRole role = new TesseractRole();
        role.setRoleName(tesseractRoleDO.getRoleName());
        role.setRoleDesc("123");
        AdminUtils.buildNewEntityCommonFields(role, currentTimeMillis, user);
        this.save(role);
        //保存菜单关联
        List<TesseractMenuDO> menuDOList = tesseractRoleDO.getMenuInfo();
        List<TesseractRoleResources> tesseractRoleResourcesList = menuDOList.stream().map(menuDO -> {
            TesseractRoleResources tesseractMenuResource = new TesseractRoleResources();
            tesseractMenuResource.setMenuId(menuDO.getMenuId());
            tesseractMenuResource.setRoleId(role.getId());
            return tesseractMenuResource;
        }).collect(Collectors.toList());
        roleResourcesService.saveBatch(tesseractRoleResourcesList);
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
