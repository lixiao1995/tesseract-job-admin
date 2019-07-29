package admin.service.impl;

import admin.entity.*;
import admin.mapper.TesseractMenuResourceMapper;
import admin.mapper.TesseractUserMapper;
import admin.pojo.DO.StatisticsLogDO;
import admin.pojo.DO.TesseractUserDO;
import admin.pojo.DO.UserDO;
import admin.pojo.VO.UserAuthVO;
import admin.pojo.VO.UserLoginVO;
import admin.security.SecurityUserDetail;
import admin.service.*;
import admin.util.AdminUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tesseract.exception.TesseractException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static admin.constant.AdminConstant.*;
import static admin.util.AdminUtils.bcryptEncode;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author nickle
 * @since 2019-07-03
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class TesseractUserServiceImpl extends ServiceImpl<TesseractUserMapper, TesseractUser> implements ITesseractUserService {
    private static final String TOKEN_FORMATTER = "tessseract-%s-%s";
    /**
     * token过期时间，默认2小时
     */
    private static final Integer TOKEN_EXPIRE_TIME = 2 * 60;
    @Autowired
    private ITesseractTokenService tokenService;

    private int statisticsDays = 7;
    @Autowired
    private UserDetailsService webUserDetailsService;
    @Autowired
    private ITesseractUserRoleService userRoleService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TesseractMenuResourceMapper tesseractMenuResourceMapper;
    @Autowired
    private ITesseractRoleBtnService roleBtnService;
    @Autowired
    private ITesseractRoleService roleService;
    @Autowired
    private ITesseractBtnResourceService btnResourceService;
    @Autowired
    private ITesseractMenuResourceService menuResourceService;


    @Override
    public UserLoginVO userLogin(UserDO userDO) {
        UserLoginVO userLoginVO = new UserLoginVO();
        // 认证
        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(userDO.getUsername(), userDO.getPassword());
        final Authentication authentication = authenticationManager.authenticate(upToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 获取认证后的信息
        SecurityUserDetail userDetail = (SecurityUserDetail) authentication.getPrincipal();
        Integer userId = userDetail.getId();
        String userName = userDetail.getName();
        LocalDateTime nowLocalDateTime = LocalDateTime.now();
        long nowTime = nowLocalDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        QueryWrapper<TesseractToken> tesseractTokenQueryWrapper = new QueryWrapper<>();
        tesseractTokenQueryWrapper.lambda().eq(TesseractToken::getUserId, userId);
        TesseractToken tesseractToken = tokenService.getOne(tesseractTokenQueryWrapper);
        String token;
        //如果token已存在
        if (tesseractToken != null) {
            //检测是否过期
            Long expireTime = tesseractToken.getExpireTime();
            if (nowTime < expireTime) {
                tesseractToken.setToken(generateToken(userDetail));
                tesseractToken.setUpdateTime(nowTime);
                tokenService.updateById(tesseractToken);
            }
            token = tesseractToken.getToken();
        } else {
            //创建新的token
            long expireTime = nowLocalDateTime.plusMinutes(TOKEN_EXPIRE_TIME).toInstant(ZoneOffset.of("+8")).toEpochMilli();
            token = generateToken(userDetail);
            tesseractToken = new TesseractToken();
            tesseractToken.setCreateTime(nowTime);
            tesseractToken.setUpdateTime(nowTime);
            tesseractToken.setExpireTime(expireTime);
            tesseractToken.setToken(token);
            tesseractToken.setUserId(userId);
            tesseractToken.setUserName(userName);
            tokenService.save(tesseractToken);
        }
        userLoginVO.setPasswordInitial(userDetail.getPasswordInitial());
        userLoginVO.setToken(token);
        userLoginVO.setUserId(userDetail.getId());
        return userLoginVO;
    }

    @Override
    @Deprecated
    public void userLogout(String token) {
        if (StringUtils.isEmpty(token)) {
            throw new TesseractException("token为空");
        }
        QueryWrapper<TesseractToken> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TesseractToken::getToken, token);
        tokenService.remove(queryWrapper);
    }

    @Override
    public IPage<TesseractUser> listByPage(Integer currentPage, Integer pageSize, TesseractUser condition, Long startCreateTime, Long endCreateTime) {
        Page<TesseractUser> page = new Page<>(currentPage, pageSize);
        QueryWrapper<TesseractUser> queryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<TesseractUser> lambda = queryWrapper.lambda();
        //日期
        if (startCreateTime != null) {
            lambda.ge(TesseractUser::getCreateTime, startCreateTime);
        }

        if (endCreateTime != null) {
            lambda.le(TesseractUser::getCreateTime, endCreateTime);
        }

        //其他
        AdminUtils.buildCondition(queryWrapper, condition);
        return page(page, queryWrapper);
    }

    @Override
    public void saveOrUpdateUser(TesseractUserDO tesseractUserDO) {
        long currentTimeMillis = System.currentTimeMillis();
        TesseractUser tesseractUser = new TesseractUser();
        List<Integer> roleIdList = tesseractUserDO.getRoleIdList();
//        BeanCopier beanCopier = BeanCopier.create(TesseractUserDO.class, TesseractUser.class, false);
//        beanCopier.copy(tesseractUserDO, tesseractUser, null);
        BeanUtils.copyProperties(tesseractUserDO, tesseractUser);
        Integer userId = tesseractUser.getId();
        if (userId != null) {
            //如果是修改密码
            if (!StringUtils.isEmpty(tesseractUser.getPassword())) {
                tesseractUser.setPassword(bcryptEncode(tesseractUser.getPassword()));
            }
            tesseractUser.setUpdateTime(currentTimeMillis);
            updateById(tesseractUser);
            //如果角色id不为空则重建
            if (roleIdList != null) {
                //删除原有角色管理表并重建
                QueryWrapper<TesseractUserRole> userRoleQueryWrapper = new QueryWrapper<>();
                userRoleQueryWrapper.lambda().eq(TesseractUserRole::getUserId, userId);
                userRoleService.remove(userRoleQueryWrapper);
                if (roleIdList.size() != 0) {
                    List<TesseractUserRole> userRoleList = roleIdList.stream().map(roleId -> {
                        TesseractUserRole tesseractUserRole = new TesseractUserRole();
                        tesseractUserRole.setRoleId(roleId);
                        tesseractUserRole.setUserId(userId);
                        return tesseractUserRole;
                    }).collect(Collectors.toList());
                    userRoleService.saveBatch(userRoleList);
                }
            }
            return;
        }
        tesseractUser.setStatus(USER_VALID);
        tesseractUser.setUpdateTime(currentTimeMillis);
        tesseractUser.setPassword(getDefaultPasswordCode());
        tesseractUser.setCreateTime(currentTimeMillis);
        this.save(tesseractUser);
        //保存用户角色关联表
        if (!CollectionUtils.isEmpty(roleIdList)) {
            List<TesseractUserRole> userRoleList = roleIdList.stream().map(roleId -> {
                TesseractUserRole tesseractUserRole = new TesseractUserRole();
                tesseractUserRole.setRoleId(roleId);
                tesseractUserRole.setUserId(tesseractUser.getId());
                return tesseractUserRole;
            }).collect(Collectors.toList());
            userRoleService.saveBatch(userRoleList);
        }
    }

    private String getDefaultPasswordCode() {
        return DEFAULT_PASSWORD_CODE;
    }

    @Override
    public void passwordRevert(Integer userId) {
        TesseractUser user = getById(userId);
        if (user == null) {
            throw new TesseractException("用户为空");
        }
        user.setPassword(getDefaultPasswordCode());
        user.setUpdateTime(System.currentTimeMillis());
        updateById(user);
    }

    @Override
    public boolean checkToken(String token) {
        QueryWrapper<TesseractToken> tokenQueryWrapper = new QueryWrapper<>();
        tokenQueryWrapper.lambda()
                .eq(TesseractToken::getToken, token)
                .gt(TesseractToken::getExpireTime, System.currentTimeMillis());
        return !(tokenService.getOne(tokenQueryWrapper) == null);
    }

    @Override
    public void validUser(Integer userId) {

        TesseractUser user = getById(userId);
        if (user == null) {
            throw new TesseractException("用户不存在");
        }
        if (user.getStatus().equals(USER_VALID)) {
            throw new TesseractException("用户已经是激活状态");
        }
        user.setStatus(USER_VALID);
        updateById(user);
    }

    @Override
    public void invalidUser(Integer userId) {
        TesseractUser user = getById(userId);
        if (user == null) {
            throw new TesseractException("用户不存在");
        }
        if (user.getStatus().equals(USER_INVALID)) {
            throw new TesseractException("用户已经是禁用状态");
        }
        user.setStatus(USER_INVALID);
        updateById(user);
    }

    @Override
    public Collection<Integer> statisticsUser() {
        LocalDate now = LocalDate.now();
        long startTime = now.minus(6, ChronoUnit.DAYS).atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long endTime = now.plus(1, ChronoUnit.DAYS).atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        List<StatisticsLogDO> statisticsLogDOList = tokenService.statisticsActiveUser(startTime, endTime);
        return AdminUtils.buildStatisticsList(statisticsLogDOList, statisticsDays);
    }

    @Override
    public void deleteUser(Integer userId) {
        TesseractUser user = getById(userId);
        if (user == null) {
            throw new TesseractException("用户不存在");
        }
        //删除用户和角色关联表
        QueryWrapper<TesseractUserRole> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper.lambda().eq(TesseractUserRole::getUserId, userId);
        userRoleService.remove(userRoleQueryWrapper);
        //删除用户
        removeById(userId);
    }


    private String generateToken(TesseractUser user) {
        return String.format(TOKEN_FORMATTER, user.getName(), UUID.randomUUID().toString().replace("-", ""));
    }

    @Override
    public UserAuthVO getUserAuthInfo(String token) {
        // TODO 统一做 token 的校验拦截，包括是否存在、已过期、token刷新等问题
        if (StringUtils.isEmpty(token)) {
            throw new TesseractException("token为空");
        }
        UserAuthVO userAuthVO = new UserAuthVO();
        QueryWrapper<TesseractToken> tesseractTokenQueryWrapper = new QueryWrapper<>();
        tesseractTokenQueryWrapper.lambda().eq(TesseractToken::getToken, token);
        TesseractToken tesseractToken = tokenService.getOne(tesseractTokenQueryWrapper);
        if (tesseractToken == null) {
            throw new TesseractException("token过期,请重新登陆");
        }
        Integer userId = tesseractToken.getUserId();
        // 获取用户信息
        TesseractUser tesseractUser = this.getById(userId);
        userAuthVO.setName(tesseractUser.getName());
        List<TesseractRole> tesseractRoles = roleService.getRoleByUserId(userId);
        List<Integer> roleIds = new ArrayList<>(tesseractRoles.size());
        List<String> roleNames = new ArrayList<>(tesseractRoles.size());
        tesseractRoles.stream().forEach(role -> {
            roleIds.add(role.getId());
            roleNames.add(role.getRoleName());
        });
        userAuthVO.setRoles(roleNames);
        // 菜单权限
        List<TesseractMenuResource> menuList = new ArrayList<>();
        // 按钮权限
        List<String> btnList = new ArrayList<>();
        // 角色不为空进行查询
        if (!CollectionUtils.isEmpty(roleIds)) {
            menuList = tesseractMenuResourceMapper.selectMenusByRole(roleIds);
        }
        //根据角色获取按钮
        if (!CollectionUtils.isEmpty(tesseractRoles)) {
            //获取角色按钮关联表
            List<Integer> roleIdList = tesseractRoles.stream().map(TesseractRole::getId).collect(Collectors.toList());
            QueryWrapper<TesseractRoleBtn> roleBtnQueryWrapper = new QueryWrapper<>();
            roleBtnQueryWrapper.lambda().in(TesseractRoleBtn::getRoleId, roleIdList);
            List<TesseractRoleBtn> roleBtnList = roleBtnService.list(roleBtnQueryWrapper);

            Collection<TesseractBtnResource> btnResourceList = Lists.newArrayList();

            List<Integer> btnResourceIdList = Lists.newArrayList();
            roleBtnList.forEach(roleBtn -> {
                btnResourceIdList.add(roleBtn.getBtnId());
            });
            //获取按钮
            if (!CollectionUtils.isEmpty(btnResourceIdList)) {
                btnResourceList = btnResourceService.listByIds(btnResourceIdList);
            }
            HashMap<Integer, TesseractMenuResource> menuMap = Maps.newHashMap();
            HashMap<Integer, TesseractBtnResource> btnMap = Maps.newHashMap();
            menuList.forEach(menuResource -> menuMap.put(menuResource.getId(), menuResource));
            btnResourceList.forEach(btnResource -> btnMap.put(btnResource.getId(), btnResource));
            for (TesseractRoleBtn tesseractRoleBtn : roleBtnList) {
                Integer btnId = tesseractRoleBtn.getBtnId();
                Integer menuId = tesseractRoleBtn.getMenuId();
                btnList.add(menuMap.get(menuId).getFullPath() + "/" + btnMap.get(btnId).getBtnCode());
            }
        }
        userAuthVO.setMenuList(menuList);
        userAuthVO.setBtnList(btnList);
        return userAuthVO;
    }
}
