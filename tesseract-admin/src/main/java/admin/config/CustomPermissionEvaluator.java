package admin.config;

import admin.constant.AdminConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

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
//        if (authentication.getPrincipal().toString().compareToIgnoreCase(AdminConstant.ANONYMOUS_TAG) != 0) {
//            // 操作资源对象 - 对应角色
//            String privilege = targetDomainObject + "-" + permission;
//            // 遍历当前用户的授权信息
//            for (GrantedAuthority authority : authentication.getAuthorities()) {
//                // 满足一项即有权访问
//                if (privilege.equalsIgnoreCase(authority.getAuthority())) {
//                    accessable = true;
//                    break;
//                }
//            }
//            return accessable;
//        }
        return true;
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
