package admin.service.impl;

import admin.entity.TesseractMenuResource;
import admin.entity.TesseractRole;
import admin.mapper.TesseractRoleMapper;
import admin.pojo.WebUserDetail;
import admin.security.SecurityUserContextHolder;
import admin.service.ITesseractRoleService;
import admin.util.AdminUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author nickle
 * @since 2019-07-10
 */
@Service
public class TesseractRoleServiceImpl extends ServiceImpl<TesseractRoleMapper, TesseractRole> implements ITesseractRoleService {

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
    public void saveOrUpdateRole(TesseractRole tesseractRole) {
        long currentTimeMillis = System.currentTimeMillis();
        WebUserDetail user = SecurityUserContextHolder.getUser();
        Integer id = tesseractRole.getId();
        if(id != null){
            AdminUtils.buildUpdateEntityCommonFields(tesseractRole,currentTimeMillis,user);
            this.updateById(tesseractRole);
        }
        AdminUtils.buildNewEntityCommonFields(tesseractRole,currentTimeMillis,user);
        this.save(tesseractRole);
    }
}
