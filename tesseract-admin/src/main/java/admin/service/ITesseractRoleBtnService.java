package admin.service;

import admin.entity.TesseractBtnResource;
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

    List<TesseractBtnResource> listBtnByRoleIdList(List<Integer> roleIdList);
}
