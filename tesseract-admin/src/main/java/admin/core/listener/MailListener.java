package admin.core.listener;

import admin.core.event.MailEvent;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;

/**
 * 〈监听发送邮件事件，发送报警邮件〉
 *
 * @author nickel
 * @create 2019/7/12
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@Slf4j
public class MailListener {
    private JavaMailSender mailSender;
    private String from;

    @Subscribe
    @AllowConcurrentEvents
    public void sendMail(MailEvent mailEvent) throws Exception {
        //建立邮件消息
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(from);
            helper.setTo(mailEvent.getTo());
            helper.setSubject(mailEvent.getSubject());
            helper.setText(mailEvent.getBody(), true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("发送邮件异常");
        }
    }
}
