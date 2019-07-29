package admin.service;

import admin.entity.TesseractUser;
import admin.pojo.DO.TesseractUserDO;
import admin.pojo.VO.UserAuthVO;
import admin.pojo.DO.UserDO;
import admin.pojo.VO.UserLoginVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author nickle
 * @since 2019-07-03
 */
public interface ITesseractUserService extends IService<TesseractUser> {
    UserLoginVO userLogin(UserDO userDO);

    void userLogout(String token);

    IPage<TesseractUser> listByPage(Integer currentPage, Integer pageSize, TesseractUser condition, Long startCreateTime, Long endCreateTime);

    void saveOrUpdateUser(TesseractUserDO tesseractUserDO);

    void validUser(Integer userId);

    void invalidUser(Integer userId);

    Collection<Integer> statisticsUser();

    void deleteUser(Integer userId);

    /**
     * 根据Token获取用户权限信息
     *
     * @param token
     * @return: admin.pojo.VO.UserAuthVO
     * @author: LeoLee
     * @date: 2019/7/10 10:16
     */
    UserAuthVO getUserAuthInfo(String token);

    void passwordRevert(Integer userId);

    boolean checkToken(String token);
}
