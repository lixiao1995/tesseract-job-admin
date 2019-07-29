package admin.core.event;

import admin.entity.TesseractJobDetail;
import admin.entity.TesseractLog;
import admin.entity.TesseractTrigger;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tesseract.core.dto.TesseractAdminJobNotify;

/**
 * @projectName: tesseract-job-admin
 * @className: RetryEvent
 * @description: 重试事件
 * @author: liangxuekai
 * @createDate: 2019-07-17 21:22
 * @updateUser: liangxuekai
 * @updateDate: 2019-07-17 21:22
 * @updateRemark: 修改内容
 * @version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetryEvent {
    private TesseractAdminJobNotify jobNotify;
    private TesseractTrigger tesseractTrigger;
    private TesseractLog tesseractLog;
}
