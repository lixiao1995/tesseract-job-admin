package admin.service.impl;

import admin.entity.TesseractRole;
import admin.entity.TesseractUserRole;
import admin.mapper.TesseractUserRoleMapper;
import admin.service.ITesseractRoleService;
import admin.service.ITesseractUserRoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
public class TesseractUserRoleServiceImpl extends ServiceImpl<TesseractUserRoleMapper, TesseractUserRole> implements ITesseractUserRoleService {
    @Autowired
    private ITesseractRoleService roleService;

    @CacheEvict(cacheNames = "tesseract-cache", key = "'user_role_'+#userId")
    @Override
    public void deleteRoleByUserId(Integer userId) {
        QueryWrapper<TesseractUserRole> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper.lambda().eq(TesseractUserRole::getUserId, userId);
        this.remove(userRoleQueryWrapper);
    }

    @Cacheable(cacheNames = "tesseract-cache", key = "'user_role_'+#userId")
    @Override
    public List<TesseractRole> getRoleByUserId(Integer userId) {
        List<TesseractRole> roleList = Lists.newArrayList();
        QueryWrapper<TesseractUserRole> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper.lambda().eq(TesseractUserRole::getUserId, userId);
        List<Integer> roleIdList = this.list(userRoleQueryWrapper).stream().map(TesseractUserRole::getRoleId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(roleIdList)) {
            QueryWrapper<TesseractRole> roleQueryWrapper = new QueryWrapper<>();
            roleQueryWrapper.lambda().in(TesseractRole::getId, roleIdList);
            roleList = roleService.list(roleQueryWrapper);
        }
        return roleList;
    }
}
