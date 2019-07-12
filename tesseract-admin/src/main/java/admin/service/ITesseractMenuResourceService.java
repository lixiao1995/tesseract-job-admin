package admin.service;

import admin.entity.TesseractMenuResource;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author nickle
 * @since 2019-07-10
 */
public interface ITesseractMenuResourceService extends IService<TesseractMenuResource> {

    IPage<TesseractMenuResource> listByPage(Integer currentPage, Integer pageSize, TesseractMenuResource condition, Long startCreateTime, Long endCreateTime);

    void saveOrUpdateMenu(TesseractMenuResource tesseractMenuResource);

}
