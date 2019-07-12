package admin.service.impl;

import admin.entity.TesseractMenuResource;
import admin.mapper.TesseractMenuResourceMapper;
import admin.security.SecurityUserDetail;
import admin.security.SecurityUserContextHolder;
import admin.service.ITesseractMenuResourceService;
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
public class TesseractMenuResourceServiceImpl extends ServiceImpl<TesseractMenuResourceMapper, TesseractMenuResource> implements ITesseractMenuResourceService {


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
        SecurityUserDetail user = SecurityUserContextHolder.getUser();
        Integer id = tesseractMenuResource.getId();
        if(id != null){
            AdminUtils.buildUpdateEntityCommonFields(tesseractMenuResource,currentTimeMillis,user);
            this.updateById(tesseractMenuResource);
        }
        AdminUtils.buildNewEntityCommonFields(tesseractMenuResource,currentTimeMillis,user);
        this.save(tesseractMenuResource);

    }
}
