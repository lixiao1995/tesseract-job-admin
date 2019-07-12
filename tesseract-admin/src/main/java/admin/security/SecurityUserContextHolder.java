package admin.security;

import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @description: 用户信息上下文工具类
 * @author: 李明
 * @company: 朴新教育
 * @version:
 * @date: 2019/7/12 12:26
 */
public class SecurityUserContextHolder {

    /**
     * 获取安全上下文用户信息
     * @return
     */
    public static SecurityUserDetail getUser() {
        return (SecurityUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
