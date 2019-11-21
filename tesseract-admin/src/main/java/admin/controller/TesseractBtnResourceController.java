package admin.controller;


import admin.constant.AdminConstant;
import admin.entity.TesseractBtnResource;
import admin.pojo.VO.BtnVO;
import admin.pojo.VO.CommonResponseVO;
import admin.pojo.VO.PageVO;
import admin.service.ITesseractBtnResourceService;
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
@RequestMapping("/tesseract-btn-resource")

public class TesseractBtnResourceController {
    @Autowired
    private ITesseractBtnResourceService btnResourceService;

    /**
     * 角色列表
     *
     * @param currentPage
     * @param pageSize
     * @param condition
     * @param startCreateTime
     * @param endCreateTime
     * @return: admin.pojo.VO.CommonResponseVO
     * @author: 李明
     * @date: 2019/7/12 15:35
     */

    @PreAuthorize("hasPermission('btn', 'select') and hasRole('" + AdminConstant.SUPER_ADMIN_NAME + "')")
    @RequestMapping("/btnList")
    public CommonResponseVO btnList(@NotNull @Min(1) Integer currentPage
            , @NotNull @Min(1) @Max(50) Integer pageSize, TesseractBtnResource condition,
                                    Long startCreateTime,
                                    Long endCreateTime) {
        IPage<TesseractBtnResource> btbIPage = btnResourceService.listByPage(currentPage, pageSize, condition, startCreateTime, endCreateTime);
        BtnVO btnVO = new BtnVO();
        PageVO pageVO = new PageVO();
        pageVO.setCurrentPage(btbIPage.getCurrent());
        pageVO.setPageSize(btbIPage.getSize());
        pageVO.setTotal(btbIPage.getTotal());
        btnVO.setPageInfo(pageVO);
        btnVO.setBtnList(btbIPage.getRecords());
        return CommonResponseVO.success(btnVO);
    }

    /**
     * @return
     */

    @PreAuthorize("hasPermission('btn', 'select') and hasRole('" + AdminConstant.SUPER_ADMIN_NAME + "')")
    @RequestMapping("/allBtn")
    public CommonResponseVO allBtn() {
        return CommonResponseVO.success(btnResourceService.list());
    }

    /**
     * @param btnResource
     * @return
     * @throws Exception
     */

    @PreAuthorize("hasPermission('btn', 'add') and hasRole('" + AdminConstant.SUPER_ADMIN_NAME + "')")
    @RequestMapping("/saveOrUpdateBtn")
    public CommonResponseVO saveOrUpdateBtn(@Validated @RequestBody TesseractBtnResource btnResource) throws Exception {
        btnResourceService.saveOrUpdateBtn(btnResource);
        return CommonResponseVO.SUCCESS;
    }


    @PreAuthorize("hasPermission('btn', 'delete') and hasRole('" + AdminConstant.SUPER_ADMIN_NAME + "')")
    @RequestMapping("/deleteBtn")
    public CommonResponseVO deleteBtn(@NotNull Integer btnId) throws Exception {
        btnResourceService.deleteBtn(btnId);
        return CommonResponseVO.SUCCESS;
    }


    @PreAuthorize("hasPermission('btn', 'select') and hasRole('" + AdminConstant.SUPER_ADMIN_NAME + "')")
    @RequestMapping("/btnListByMenuIdAndRoleId")
    public CommonResponseVO btnListByMenuIdAndRoleId(@NotNull Integer roleId, @NotNull Integer menuId) {
        return CommonResponseVO.success(btnResourceService.btnListByMenuIdAndRoleId(roleId, menuId));
    }

}
