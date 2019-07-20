package admin.service.impl;

import admin.entity.TesseractBtnResource;
import admin.entity.TesseractBtnResource;
import admin.entity.TesseractMenuBtn;
import admin.entity.TesseractMenuResource;
import admin.mapper.TesseractBtnResourceMapper;
import admin.security.SecurityUserContextHolder;
import admin.security.SecurityUserDetail;
import admin.service.ITesseractBtnResourceService;
import admin.service.ITesseractMenuBtnService;
import admin.service.ITesseractMenuResourceService;
import admin.util.AdminUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tesseract.exception.TesseractException;

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
    private ITesseractMenuBtnService menuBtnService;
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
            Integer newMenuId = btnResource.getMenuId();
            if (tesseractBtnResource == null) {
                throw new TesseractException("按钮不存在");
            }
            AdminUtils.buildUpdateEntityCommonFields(btnResource, currentTimeMillis, user);
            Integer menuId = tesseractBtnResource.getMenuId();
            //如果没有改变菜单则不需要更改
            if (menuId.equals(newMenuId)) {
                this.updateById(btnResource);
                return;
            }
            //更改菜单
            TesseractMenuResource menuResource = menuResourceService.getById(newMenuId);
            btnResource.setMenuName(menuResource.getMetaTitle());
            btnResource.setMenuPath(menuResource.getPath());
            this.updateById(btnResource);
            //维护中间表
            TesseractMenuBtn tesseractMenuBtn = new TesseractMenuBtn();
            tesseractMenuBtn.setBtnId(id);
            tesseractMenuBtn.setMenuId(menuResource.getId());
            menuBtnService.save(tesseractMenuBtn);
            return;
        }
        AdminUtils.buildNewEntityCommonFields(btnResource, currentTimeMillis, user);
        TesseractMenuResource menuResource = menuResourceService.getById(btnResource.getMenuId());
        btnResource.setMenuName(menuResource.getMetaTitle());
        btnResource.setMenuPath(menuResource.getPath());
        this.save(btnResource);
        //维护中间表
        TesseractMenuBtn tesseractMenuBtn = new TesseractMenuBtn();
        tesseractMenuBtn.setBtnId(btnResource.getId());
        tesseractMenuBtn.setMenuId(menuResource.getId());
        menuBtnService.save(tesseractMenuBtn);
    }

    @Override
    public void deleteBtn(Integer btnId) {
        //删除菜单按钮关联表
        QueryWrapper<TesseractMenuBtn> menuBtnQueryWrapper = new QueryWrapper<>();
        menuBtnQueryWrapper.lambda().eq(TesseractMenuBtn::getBtnId, btnId);
        menuBtnService.remove(menuBtnQueryWrapper);
        //删除按钮
        removeById(btnId);
    }
}
