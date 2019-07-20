package admin.mapper;

import admin.entity.TesseractBtnResource;
import admin.entity.TesseractMenuBtn;
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
public interface TesseractMenuBtnMapper extends BaseMapper<TesseractMenuBtn> {
    List<TesseractBtnResource> listBtnByMenuIdList(@Param("menuIdList") List<Integer> menuIdList);
}
