package admin.service;

import admin.entity.TesseractGroup;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author nickle
 * @since 2019-07-10
 */
public interface ITesseractGroupService extends IService<TesseractGroup> {
    /**
     * 删除组
     *
     * @param groupId
     */
    void deleteGroup(Integer groupId);

    /**
     * 更新或保存
     *
     * @param tesseractGroup
     */
    void saveOrUpdateGroup(TesseractGroup tesseractGroup);

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
    IPage<TesseractGroup> listByPage(Integer currentPage, Integer pageSize, TesseractGroup condition, Long startCreateTime, Long endCreateTime);

    /**
     * 拿到所有组
     *
     * @return
     */
    List<TesseractGroup> allGroup();
}
