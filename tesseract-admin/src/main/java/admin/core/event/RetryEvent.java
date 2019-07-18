package admin.core.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

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
@Component
public class RetryEvent  extends ApplicationEvent {

    public RetryEvent(Object source) {
        super(source);
    }
}
