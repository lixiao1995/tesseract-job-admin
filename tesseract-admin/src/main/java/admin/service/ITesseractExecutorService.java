package admin.service;

import admin.entity.TesseractExecutor;
import admin.pojo.VO.ExecutorVO;
import com.baomidou.mybatisplus.extension.service.IService;
import tesseract.core.dto.TesseractAdminRegistryRequest;
import tesseract.core.dto.TesseractAdminRegistryResDTO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author nickle
 * @since 2019-07-03
 */
public interface ITesseractExecutorService extends IService<TesseractExecutor> {
    /**
     * 注册executor detail，绑定到触发器对应的executor
     *
     * @param tesseractAdminRegistryRequest
     * @return
     * @throws Exception
     */
    TesseractAdminRegistryResDTO registry(TesseractAdminRegistryRequest tesseractAdminRegistryRequest) throws Exception;

    /**
     * 分页获取executor执行器
     *
     * @param currentPage
     * @param pageSize
     * @param condition
     * @param startCreateTime
     * @param endCreateTime
     * @return
     */
    ExecutorVO listByPage(Long currentPage, Long pageSize,
                          TesseractExecutor condition,
                          Long startCreateTime,
                          Long endCreateTime);

    /**
     * 根据id更新或添加执行器
     *
     * @param tesseractExecutor
     */
    void saveOrUpdateExecutor(TesseractExecutor tesseractExecutor);

    /**
     * 删除执行器
     *
     * @param executorId
     */
    void deleteExecutor(Integer executorId);

    /**
     * 根据组id获取到所有执行器，这里不包括executor detail 即执行器下面的机器
     *
     * @param groupId
     * @return
     */
    List<TesseractExecutor> executorListNoDetail(Integer groupId);
}
