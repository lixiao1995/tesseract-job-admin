package admin.service;

import admin.entity.TesseractRole;
import admin.entity.TesseractUserRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author nickle
 * @since 2019-07-10
 */
public interface ITesseractUserRoleService extends IService<TesseractUserRole> {
    /**
     * 根据用户id删除用户角色
     *
     * @param userId
     */
    void deleteRoleByUserId(Integer userId);

    /**
     * 根据用户id获取用户所有角色
     *
     * @param userId
     * @return
     */
    List<TesseractRole> getRoleByUserId(Integer userId);
}
