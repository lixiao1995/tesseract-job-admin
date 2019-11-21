package admin.service;

import admin.entity.TesseractRoleBtn;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author nickle
 * @since 2019-07-19
 */
public interface ITesseractRoleBtnService extends IService<TesseractRoleBtn> {
    /**
     * 统计所有权限
     *
     * @param roleNameList
     * @param menuCode
     * @param btnCode
     * @return
     */
    Integer countPermissions(List<String> roleNameList, String menuCode, String btnCode);
}
