package admin.service;

import admin.entity.TesseractBtnResource;
import admin.entity.TesseractRole;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author nickle
 * @since 2019-07-11
 */
public interface ITesseractBtnResourceService extends IService<TesseractBtnResource> {


    IPage<TesseractBtnResource> listByPage(Integer currentPage, Integer pageSize, TesseractBtnResource condition, Long startCreateTime, Long endCreateTime);

    void saveOrUpdateBtn(TesseractBtnResource btnResource);

    void deleteBtn(Integer btnId);

    Collection<TesseractBtnResource> btnListByMenuIdAndRoleId(Integer roleId, Integer menuId);
}
