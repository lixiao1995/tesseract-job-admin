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

    IPage<TesseractRole> listByPage(Integer currentPage, Integer pageSize, TesseractRole condition, Long startCreateTime, Long endCreateTime);

    void saveOrUpdateRole(TesseractRoleDO tesseractRoleDO);

    void deleteRole(Integer roleId);

    List<Integer> getRoleMenuIdList(Integer roleId);

}
