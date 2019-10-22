package admin.config;

import admin.constant.AdminConstant;
import admin.service.ITesseractRoleBtnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @description: 自定义资源权限校验器
 * @author: 李明
 * @company: 朴新教育
 * @version:
 * @date: 2019/7/12 16:52
 */
@Slf4j
@Configuration
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    private ITesseractRoleBtnService tesseractRoleBtnService;

    /**
     * 普通的targetDomainObject判断
     *
     * @param authentication
     * @param targetDomainObject
     * @param permission
     * @return
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        boolean accessable = false;
        log.info("进行PermissionEvaluator校验...");
        // 自定义匹配规则
        if (authentication.getPrincipal().toString().compareToIgnoreCase(AdminConstant.ANONYMOUS_TAG) != 0) {

            // 操作资源对象 - 对应角色
            // String privilege = targetDomainObject + "-" + permission;
            // 为空，说明不需要进行权限鉴权
            if (targetDomainObject == null || StringUtils.isEmpty(permission)) {
                return true;
            }

            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            List<String> roleNames = new ArrayList<>();
            // 遍历当前用户的授权信息
            for (GrantedAuthority authority : authorities) {
                roleNames.add(authority.getAuthority());
            }
            // 角色为空，无权限
            if (roleNames.isEmpty()) {
                return false;
            }
            String menuCode = (String) targetDomainObject;
            String btnCode = (String) permission;
            int count = tesseractRoleBtnService.countPermissions(roleNames, menuCode, btnCode);
            if (count > 0) {
                accessable = true;
            }
            return accessable;
        }
        return false;
    }

    /**
     * 用于ACL的访问控制
     *
     * @param authentication
     * @param serializable
     * @param s
     * @param o
     * @return
     */
    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
        return true;
    }
}
