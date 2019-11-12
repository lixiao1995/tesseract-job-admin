package admin.security;

import admin.entity.TesseractRole;
import admin.entity.TesseractUser;
import admin.service.ITesseractRoleService;
import admin.service.ITesseractUserRoleService;
import admin.service.ITesseractUserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import tesseract.exception.TesseractException;

import javax.annotation.Resource;
import java.util.List;

import static admin.constant.AdminConstant.DEFAULT_PASSWORD_CODE;
import static admin.constant.AdminConstant.USER_INVALID;

/**
 * @description: security 登录
 * @author: LeoLee nickle
 * @company: ***
 * @version:
 * @date: 2019/7/9 14:28
 */
@Service
public class SecurityUserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private ITesseractUserService userService;
    @Resource
    private ITesseractUserRoleService userRoleService;

    /**
     * 根据用户名登录
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO 数据库中获取用户密码，角色，资源等信息
        TesseractUser tesseractUser = userService.getUserByName(username);
        if (ObjectUtils.isEmpty(tesseractUser)) {
            throw new UsernameNotFoundException("用户登录，用户信息查询失败");
        }
        //验证是否停用
        if (USER_INVALID.equals(tesseractUser.getStatus())) {
            throw new TesseractException("用户已停用");
        }
        Integer userId = tesseractUser.getId();
        List<TesseractRole> roleList = userRoleService.getRoleByUserId(userId);
        if (CollectionUtils.isEmpty(roleList)) {
            throw new TesseractException("用户角色为空");
        }
        // TODO 封装为框架使用的 userDetail，如果需要额外的用户信息，自行添加
        SecurityUserDetail webUserDetail = new SecurityUserDetail();
        webUserDetail.setId(userId);
        webUserDetail.setPassword(tesseractUser.getPassword());
        webUserDetail.setName(tesseractUser.getName());
        webUserDetail.setRoleList(roleList);
        webUserDetail.setGroupId(tesseractUser.getGroupId());
        webUserDetail.setGroupName(tesseractUser.getGroupName());
        webUserDetail.setPasswordInitial(DEFAULT_PASSWORD_CODE.equals(tesseractUser.getPassword()));
        return webUserDetail;
    }
}
