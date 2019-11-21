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

    /**
     * 分页获取所有按钮资源
     *
     * @param currentPage
     * @param pageSize
     * @param condition
     * @param startCreateTime
     * @param endCreateTime
     * @return
     */
    IPage<TesseractBtnResource> listByPage(Integer currentPage, Integer pageSize, TesseractBtnResource condition, Long startCreateTime, Long endCreateTime);

    /**
     * 根据按钮ID更新或保存按钮
     * 注意：目前已经废弃，由于权限通过写死按钮标识符
     *
     * @param btnResource
     */
    @Deprecated
    void saveOrUpdateBtn(TesseractBtnResource btnResource);

    /**
     * 根据id删除按钮
     * 注意：目前已经废弃，由于权限通过写死按钮标识符
     *
     * @param btnId
     */
    @Deprecated
    void deleteBtn(Integer btnId);

    /**
     * 根据菜单和角色获取按钮权限
     *
     * @param roleId
     * @param menuId
     * @return
     */
    Collection<TesseractBtnResource> btnListByMenuIdAndRoleId(Integer roleId, Integer menuId);
}
