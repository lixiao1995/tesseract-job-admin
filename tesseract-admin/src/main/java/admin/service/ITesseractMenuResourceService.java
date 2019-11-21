package admin.service;

import admin.entity.TesseractMenuResource;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author nickle
 * @since 2019-07-10
 */
public interface ITesseractMenuResourceService extends IService<TesseractMenuResource> {
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
    IPage<TesseractMenuResource> listByPage(Integer currentPage, Integer pageSize, TesseractMenuResource condition, Long startCreateTime, Long endCreateTime);

    /**
     * 保存或更新
     *
     * @param tesseractMenuResource
     */
    void saveOrUpdateMenu(TesseractMenuResource tesseractMenuResource);

    /**
     * 删除
     *
     * @param menuId
     */
    void deleteMenu(Integer menuId);
}
