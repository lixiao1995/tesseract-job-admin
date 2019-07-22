package admin.security;

import admin.entity.TesseractRole;
import admin.entity.TesseractUser;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: Security使用用户类
 * @author: 李明
 * @company: 朴新教育
 * @version:
 * @date: 2019/7/9 15:25
 */
@Data
public class SecurityUserDetail extends TesseractUser implements UserDetails {

    /**
     * 角色
     */
    private List<TesseractRole> roleList;

    /**
     * 获取权限信息
     *
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        /**
         * 将角色信息封装为框架要求格式
         */
        if (roleList == null) {
            return null;
        }
        return roleList.stream().map(
                s -> new SimpleGrantedAuthority(s.getRoleName())
        ).collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
