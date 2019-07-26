package admin.mapper;

import admin.entity.Permission;
import admin.entity.TesseractBtnResource;
import admin.entity.TesseractRoleBtn;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author nickle
 * @since 2019-07-19
 */
public interface TesseractRoleBtnMapper extends BaseMapper<TesseractRoleBtn> {

    List<TesseractBtnResource> listBtnByRoleIdList(@Param("roleIdList") List<Integer> roleIdList);

    /**
     * 查看是否拥有该权限
     * @param roleNameList
     * @return
     */
    Integer countPermissions(@Param("roleNameList") List<String> roleNameList,
                         @Param("menuCode") String menuCode,
                         @Param("btnCode") String btnCode);

}
