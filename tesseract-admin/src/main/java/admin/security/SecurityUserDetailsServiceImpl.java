package admin.security;

import admin.entity.TesseractRole;
import admin.entity.TesseractUser;
import admin.mapper.TesseractRoleMapper;
import admin.mapper.TesseractUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description: security 登录
 * @author: LeoLee
 * @company: ***
 * @version:
 * @date: 2019/7/9 14:28
 */
@Service
public class SecurityUserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private TesseractUserMapper tesseractUserMapper;
    @Resource
    private TesseractRoleMapper tesseractRoleMapper;

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
        QueryWrapper<TesseractUser> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(TesseractUser::getName, username);
        TesseractUser tesseractUser = tesseractUserMapper.selectOne(queryWrapper);
        if (ObjectUtils.isEmpty(tesseractUser)) {
            throw new UsernameNotFoundException("用户登录，用户信息查询失败");
        }
        Integer userId = tesseractUser.getId();
        List<TesseractRole> roleList = tesseractRoleMapper.listRoleByUserId(userId);

        // TODO 封装为框架使用的 userDetail，如果需要额外的用户信息，自行添加
        SecurityUserDetail webUserDetail = new SecurityUserDetail();
        webUserDetail.setId(userId);
        webUserDetail.setPassword(tesseractUser.getPassword());
        webUserDetail.setName(tesseractUser.getName());
        webUserDetail.setRoleList(roleList);
        webUserDetail.setGroupId(tesseractUser.getGroupId());
        webUserDetail.setGroupName(tesseractUser.getGroupName());
        return webUserDetail;
    }
}
