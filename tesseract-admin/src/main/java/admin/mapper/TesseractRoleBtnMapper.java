package admin.mapper;

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
}
