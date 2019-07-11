package admin.core.listener;

import admin.core.event.MailEvent;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * 〈监听发送邮件事件，发送报警邮件〉
 *
 * @author nickel
 * @create 2019/7/12
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
public class MailListener {
    private JavaMailSender mailSender;

    @Subscribe
    @AllowConcurrentEvents
    public void sendMail(MailEvent mailEvent) {
        //建立邮件消息
        SimpleMailMessage mainMessage = new SimpleMailMessage();
        //发送者
        mainMessage.setFrom(mailEvent.getFrom());
        //接收者
        mainMessage.setTo(mailEvent.getTo());
        //发送的标题
        mainMessage.setSubject(mailEvent.getSubject());
        //发送的内容
        mainMessage.setText(mailEvent.getBody());
        mailSender.send(mainMessage);
    }
}
