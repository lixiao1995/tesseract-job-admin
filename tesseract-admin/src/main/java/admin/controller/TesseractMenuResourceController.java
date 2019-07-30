package admin.controller;


import admin.annotation.TokenCheck;
import admin.constant.AdminConstant;
import admin.entity.TesseractMenuResource;
import admin.pojo.VO.CommonResponseVO;
import admin.pojo.VO.MenuVO;
import admin.pojo.VO.PageVO;
import admin.service.ITesseractMenuResourceService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author nickle
 * @since 2019-07-10
 */
@RestController
@RequestMapping("/tesseract-menu")
public class TesseractMenuResourceController {

    @Autowired
    private ITesseractMenuResourceService tesseractMenuResourceService;

    /**
     * 菜单列表
     *
     * @param currentPage
     * @param pageSize
     * @param condition
     * @param startCreateTime
     * @param endCreateTime
     * @return: admin.pojo.VO.CommonResponseVO
     * @author: LeoLee
     * @date: 2019/7/12
     */
    @TokenCheck
    @PreAuthorize("hasPermission('menu', 'select') and hasRole('" + AdminConstant.SUPER_ADMIN_NAME + "')")
    @RequestMapping("/menuList")
    public CommonResponseVO menuList(@NotNull @Min(1) Integer currentPage
            , @NotNull @Min(1) @Max(50) Integer pageSize, TesseractMenuResource condition,
                                     Long startCreateTime,
                                     Long endCreateTime) {
        IPage<TesseractMenuResource> userIPage = tesseractMenuResourceService.listByPage(currentPage, pageSize, condition, startCreateTime, endCreateTime);
        MenuVO menuVO = new MenuVO();
        PageVO pageVO = new PageVO();
        pageVO.setCurrentPage(userIPage.getCurrent());
        pageVO.setPageSize(userIPage.getSize());
        pageVO.setTotal(userIPage.getTotal());
        menuVO.setPageInfo(pageVO);
        menuVO.setMenuList(userIPage.getRecords());
        return CommonResponseVO.success(menuVO);
    }

    /**
     * 查询全部菜单
     *
     * @return
     */
    @TokenCheck
    @PreAuthorize("hasPermission('menu', 'select') and hasRole('" + AdminConstant.SUPER_ADMIN_NAME + "')")
    @RequestMapping("/allMenu")
    public CommonResponseVO allMenu() {
        return CommonResponseVO.success(tesseractMenuResourceService.list());
    }

    /**
     * 保存修改菜单
     *
     * @param tesseractMenuResource
     * @return
     * @throws Exception
     */
    @TokenCheck
    @PreAuthorize("hasPermission('menu', 'add') and hasRole('" + AdminConstant.SUPER_ADMIN_NAME + "')")
    @RequestMapping("/saveOrUpdateMenu")
    public CommonResponseVO saveOrUpdateMenu(@Validated @RequestBody TesseractMenuResource tesseractMenuResource) throws Exception {
        tesseractMenuResourceService.saveOrUpdateMenu(tesseractMenuResource);
        return CommonResponseVO.SUCCESS;
    }
    @TokenCheck
    @PreAuthorize("hasPermission('menu', 'delete') and hasRole('" + AdminConstant.SUPER_ADMIN_NAME + "')")
    @RequestMapping("/deleteMenu")
    public CommonResponseVO deleteMenu(@NotNull Integer menuId) throws Exception {
        tesseractMenuResourceService.deleteMenu(menuId);
        return CommonResponseVO.SUCCESS;
    }
}
