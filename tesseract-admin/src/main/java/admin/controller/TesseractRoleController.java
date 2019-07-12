package admin.controller;


import admin.entity.TesseractMenuResource;
import admin.entity.TesseractRole;
import admin.pojo.CommonResponseVO;
import admin.pojo.MenuVO;
import admin.pojo.PageVO;
import admin.pojo.RoleVO;
import admin.service.ITesseractMenuResourceService;
import admin.service.ITesseractRoleService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author nickle
 * @since 2019-07-10
 */
@RestController
@RequestMapping("/tesseract-role")
public class TesseractRoleController {

    @Autowired
    private ITesseractRoleService tesseractRoleService;

    /**
     * 角色列表
     * @param currentPage
     * @param pageSize
     * @param condition
     * @param startCreateTime
     * @param endCreateTime
     * @return: admin.pojo.CommonResponseVO
     * @author: 李明
     * @date: 2019/7/12 15:35
     */
    @PostMapping("/roleList")
    public CommonResponseVO roleList(@NotNull @Min(1) Integer currentPage
            , @NotNull @Min(1) @Max(50) Integer pageSize, TesseractRole condition,
                                     Long startCreateTime,
                                     Long endCreateTime) {
        IPage<TesseractRole> roleIPage = tesseractRoleService.listByPage(currentPage,pageSize,condition,startCreateTime,endCreateTime);
        RoleVO roleVO = new RoleVO();
        PageVO pageVO = new PageVO();
        pageVO.setCurrentPage(roleIPage.getCurrent());
        pageVO.setPageSize(roleIPage.getSize());
        pageVO.setTotal(roleIPage.getTotal());
        roleVO.setPageInfo(pageVO);
        roleVO.setRoleList(roleIPage.getRecords());
        return CommonResponseVO.success(roleVO);
    }


    @PostMapping("/addRole")
    public CommonResponseVO addRole(@Validated @RequestBody TesseractRole tesseractRole) throws Exception {
        tesseractRoleService.saveOrUpdateRole(tesseractRole);
        return CommonResponseVO.SUCCESS;
    }


    @PostMapping("/editRole")
    public CommonResponseVO editRole(@Validated @RequestBody TesseractRole tesseractRole) throws Exception {
        tesseractRoleService.saveOrUpdateRole(tesseractRole);
        return CommonResponseVO.SUCCESS;
    }

}
