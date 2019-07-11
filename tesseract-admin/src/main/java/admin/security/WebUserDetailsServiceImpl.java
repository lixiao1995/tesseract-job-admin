package admin.security;

import admin.entity.TesseractUser;
import admin.mapper.TesseractRoleMapper;
import admin.mapper.TesseractUserMapper;
import admin.pojo.WebUserDetail;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @description:  security 登录
 * @author: LeoLee
 * @company: ***
 * @version:
 * @date: 2019/7/9 14:28
 */
@Service
public class WebUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private TesseractUserMapper tesseractUserMapper;
    @Autowired
    private TesseractRoleMapper tesseractRoleMapper;

    /**
     * 根据用户名登录
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO 数据库中获取用户密码，角色等信息
        QueryWrapper<TesseractUser> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(TesseractUser::getName, username);
        TesseractUser tesseractUser = tesseractUserMapper.selectOne(queryWrapper);
        if (ObjectUtils.isEmpty(tesseractUser)) {
            throw new UsernameNotFoundException("用户登录，用户信息查询失败");
        }
        Integer userId = tesseractUser.getId();
        List<String> roleList = tesseractRoleMapper.selectRoleCodesByUserId(userId);

        // TODO 封装为框架使用的 userDetail，如果需要额外的用户信息，自行添加
        WebUserDetail webUserDetail = new WebUserDetail();
        webUserDetail.setId(userId);
        webUserDetail.setPassword(tesseractUser.getPassword());
        webUserDetail.setName(tesseractUser.getName());
        webUserDetail.setRoleList(roleList);
        return webUserDetail;
    }
}
