package admin.controller;


import admin.entity.TesseractRole;
import admin.pojo.DO.TesseractRoleDO;
import admin.pojo.VO.CommonResponseVO;
import admin.pojo.VO.PageVO;
import admin.pojo.VO.RoleVO;
import admin.service.ITesseractRoleService;
import admin.service.ITesseractUserRoleService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 前端控制器
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
    @Autowired
    private ITesseractUserRoleService userRoleService;

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
    @RequestMapping("/roleList")
    public CommonResponseVO roleList(@NotNull @Min(1) Integer currentPage
            , @NotNull @Min(1) @Max(50) Integer pageSize, TesseractRole condition,
                                     Long startCreateTime,
                                     Long endCreateTime) {
        IPage<TesseractRole> roleIPage = tesseractRoleService.listByPage(currentPage, pageSize, condition, startCreateTime, endCreateTime);
        RoleVO roleVO = new RoleVO();
        PageVO pageVO = new PageVO();
        pageVO.setCurrentPage(roleIPage.getCurrent());
        pageVO.setPageSize(roleIPage.getSize());
        pageVO.setTotal(roleIPage.getTotal());
        roleVO.setPageInfo(pageVO);
        roleVO.setRoleList(roleIPage.getRecords());
        return CommonResponseVO.success(roleVO);
    }

    @RequestMapping("/allRole")
    public CommonResponseVO allRole() {
        return CommonResponseVO.success(tesseractRoleService.list());
    }

    @RequestMapping("/saveOrUpdateRole")
    public CommonResponseVO addRole(@Validated @RequestBody TesseractRoleDO tesseractRoleDO) throws Exception {
        tesseractRoleService.saveOrUpdateRole(tesseractRoleDO);
        return CommonResponseVO.SUCCESS;
    }

    @RequestMapping("/deleteRole")
    public CommonResponseVO deleteRole(@NotNull Integer roleId) throws Exception {
        tesseractRoleService.deleteRole(roleId);
        return CommonResponseVO.SUCCESS;
    }

    @RequestMapping("/getRoleMenu")
    public CommonResponseVO getRoleMenu(@NotNull Integer roleId) throws Exception {
        List<Integer> menuIdList = tesseractRoleService.getRoleMenuIdList(roleId);
        return CommonResponseVO.success(menuIdList);
    }

    @RequestMapping("/getRoleByUserId")
    public CommonResponseVO getRoleByUserId(@NotNull Integer userId) throws Exception {
        return CommonResponseVO.success(userRoleService.getRoleByUserId(userId));
    }
}
