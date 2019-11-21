package admin.service;

import admin.entity.TesseractRole;
import admin.pojo.DO.TesseractRoleDO;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
public interface ITesseractRoleService extends IService<TesseractRole> {
    /**
     * 分页查询
     *
     * @param currentPage
     * @param pageSize
     * @param condition
     * @param startCreateTime
     * @param endCreateTime
     * @return
     */
    IPage<TesseractRole> listByPage(Integer currentPage, Integer pageSize, TesseractRole condition, Long startCreateTime, Long endCreateTime);

    /**
     * 保存或更新
     *
     * @param tesseractRoleDO
     */
    void saveOrUpdateRole(TesseractRoleDO tesseractRoleDO);

    /**
     * 删除角色
     *
     * @param roleId
     */
    void deleteRole(Integer roleId);

    /**
     * 获取角色下所有菜单id
     *
     * @param roleId
     * @return
     */
    List<Integer> getRoleMenuIdList(Integer roleId);

}
