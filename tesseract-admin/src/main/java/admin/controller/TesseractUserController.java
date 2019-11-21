package admin.controller;


import admin.entity.TesseractUser;
import admin.pojo.DO.TesseractUserDO;
import admin.pojo.DO.UserDO;
import admin.pojo.DO.UserLoginDO;
import admin.pojo.VO.CommonResponseVO;
import admin.pojo.VO.PageVO;
import admin.pojo.VO.UserVO;
import admin.security.SecurityUserDetail;
import admin.service.ITesseractUserService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tesseract.exception.TesseractException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author nickle
 * @since 2019-07-03
 */
@RestController
@RequestMapping("/tesseract-user")
public class TesseractUserController {
    @Autowired
    private ITesseractUserService tesseractUserService;

    @RequestMapping("/login")
    public CommonResponseVO login(@Validated @RequestBody UserDO userDO) {
        return CommonResponseVO.success(tesseractUserService.userLogin(userDO));
    }

    /**
     * 改由spring security来做
     *
     * @param httpServletRequest
     * @return
     */
    @Deprecated
    @RequestMapping("/logout")
    public CommonResponseVO logout(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("X-Token");
        tesseractUserService.userLogout(token);
        return CommonResponseVO.SUCCESS;
    }

    @RequestMapping("/userList")
    public CommonResponseVO userList(@NotNull @Min(1) Integer currentPage
            , @NotNull @Min(1) @Max(50) Integer pageSize, TesseractUser condition,
                                     Long startCreateTime,
                                     Long endCreateTime) {
        // TODO 所有使用当前用户信息的地方可以统一获取
        SecurityUserDetail webUserDetail = (SecurityUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(">>>>> UserContextHolder中获取的用户信息: " + JSON.toJSONString(webUserDetail));
        IPage<TesseractUser> userIPage = tesseractUserService.listByPage(currentPage, pageSize, condition, startCreateTime, endCreateTime);
        UserVO userVO = new UserVO();
        PageVO pageVO = new PageVO();
        pageVO.setCurrentPage(userIPage.getCurrent());
        pageVO.setPageSize(userIPage.getSize());
        pageVO.setTotal(userIPage.getTotal());
        userVO.setPageInfo(pageVO);
        userVO.setUserList(userIPage.getRecords());
        return CommonResponseVO.success(userVO);
    }

    @RequestMapping("/addUser")
    public CommonResponseVO addUser(@Validated @RequestBody TesseractUserDO tesseractUserDO) throws Exception {
        tesseractUserService.saveOrUpdateUser(tesseractUserDO);
        return CommonResponseVO.SUCCESS;
    }

    @RequestMapping("/modifyPassword")
    public CommonResponseVO modifyPassword(@Validated @RequestBody UserLoginDO userLoginDO) throws Exception {
        TesseractUserDO tesseractUserDO = new TesseractUserDO();
        tesseractUserDO.setId(userLoginDO.getId());
        tesseractUserDO.setPassword(userLoginDO.getPassword());
        tesseractUserService.saveOrUpdateUser(tesseractUserDO);
        return CommonResponseVO.SUCCESS;
    }

    @RequestMapping("/passwordRevert")
    public CommonResponseVO passwordRevert(@NotNull Integer userId) throws Exception {
        tesseractUserService.passwordRevert(userId);
        return CommonResponseVO.SUCCESS;
    }

    @RequestMapping("/validUser")
    public CommonResponseVO validUser(@NotNull Integer userId) throws Exception {
        tesseractUserService.validUser(userId);
        return CommonResponseVO.SUCCESS;
    }

    @RequestMapping("/invalidUser")
    public CommonResponseVO invalidUser(@NotNull Integer userId) throws Exception {
        tesseractUserService.invalidUser(userId);
        return CommonResponseVO.SUCCESS;
    }

    @RequestMapping("/deleteUser")
    public CommonResponseVO deleteUser(@NotNull Integer userId) throws Exception {
        tesseractUserService.deleteUser(userId);
        return CommonResponseVO.SUCCESS;
    }

    @RequestMapping("/getUserCount")
    public CommonResponseVO getUserCount() {
        return CommonResponseVO.success(tesseractUserService.count());
    }

    /**
     * 统计最近七天的数据
     *
     * @return
     */
    @RequestMapping("/statisticsUser")
    public CommonResponseVO statisticsUser() {
        return CommonResponseVO.success(tesseractUserService.statisticsUser());
    }

    /**
     * 获取用户权限信息，首次从数据库获取，后期考虑Redis中获取
     *
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/getUserAuthInfo")
    public CommonResponseVO getUserInfo(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("X-Token");
        if (StringUtils.isEmpty(token)) {
            throw new TesseractException(TesseractException.TOKEN_INVALID_STATUS, "token为空");
        }
        return CommonResponseVO.success(tesseractUserService.getUserAuthInfo(token));
    }
}

