package admin.service;

import admin.entity.TesseractBtnResource;
import admin.entity.TesseractMenuBtn;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author nickle
 * @since 2019-07-19
 */
public interface ITesseractMenuBtnService extends IService<TesseractMenuBtn> {

    List<TesseractBtnResource> listBtnByMenuIdList(List<Integer> menuIdList);
}
