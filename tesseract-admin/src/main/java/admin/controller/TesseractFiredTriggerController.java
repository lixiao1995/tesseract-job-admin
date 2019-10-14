package admin.controller;


import admin.annotation.TokenCheck;
import admin.entity.TesseractFiredJob;
import admin.pojo.VO.CommonResponseVO;
import admin.pojo.VO.FiredTriggerVO;
import admin.service.ITesseractFiredJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/tesseract-firedTrigger")
@Validated
public class TesseractFiredTriggerController {
    @Autowired
    private ITesseractFiredJobService firedJobService;

    @TokenCheck
    @RequestMapping("/firedTriggerList")
    public CommonResponseVO firedTriggerList(@NotNull @Min(1) Long currentPage
            , @NotNull @Min(1) @Max(50) Long pageSize, TesseractFiredJob condition) {
        FiredTriggerVO firedTrigger = firedJobService.findFiredTrigger(currentPage, pageSize, condition);
        return CommonResponseVO.success(firedTrigger);
    }

    @TokenCheck
    @RequestMapping("/stop")
    public CommonResponseVO stopFiredTrigger(@NotNull Integer firedTriggerId) throws Exception {
        firedJobService.stopFiredJob(firedTriggerId);
        return CommonResponseVO.SUCCESS;
    }
}

