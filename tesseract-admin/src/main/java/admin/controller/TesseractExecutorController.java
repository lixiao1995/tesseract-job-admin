package admin.controller;


import admin.annotation.TokenCheck;
import admin.entity.TesseractExecutor;
import admin.pojo.VO.CommonResponseVO;
import admin.pojo.VO.ExecutorVO;
import admin.service.ITesseractExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author nickle
 * @since 2019-07-03
 */
@RestController
@RequestMapping("/tesseract-executor")
@Validated
public class TesseractExecutorController {
    @Autowired
    private ITesseractExecutorService tesseractExecutorService;

    @TokenCheck
    @RequestMapping("/executorList")
    public CommonResponseVO executorList(@NotNull @Min(1) Long currentPage
            , @NotNull @Min(1) @Max(50) Long pageSize, TesseractExecutor condition,
                                         Long startCreateTime,
                                         Long endCreateTime) {
        ExecutorVO executorVO = tesseractExecutorService.listByPage(currentPage, pageSize
                , condition, startCreateTime, endCreateTime);
        return CommonResponseVO.success(executorVO);
    }

    @TokenCheck
    @RequestMapping("/executorListNoDetail")
    public CommonResponseVO executorListNoDetail(Integer groupId) {
        return CommonResponseVO.success(tesseractExecutorService.executorListNoDetail(groupId));
    }

    @TokenCheck
    @RequestMapping("/addExecutor")
    public CommonResponseVO addExecutor(@Validated @RequestBody TesseractExecutor tesseractExecutor) throws Exception {
        tesseractExecutorService.saveOrUpdateExecutor(tesseractExecutor);
        return CommonResponseVO.SUCCESS;
    }

    @TokenCheck
    @RequestMapping("/deleteExecutor")
    public CommonResponseVO deleteExecutor(@NotNull Integer executorId) throws Exception {
        tesseractExecutorService.deleteExecutor(executorId);
        return CommonResponseVO.SUCCESS;
    }
}

