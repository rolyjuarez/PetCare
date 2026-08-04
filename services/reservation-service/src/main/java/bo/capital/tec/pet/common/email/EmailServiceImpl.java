package bo.capital.tec.pet.common.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.enabled:true}")
    private boolean enabled;

    @Value("${app.email.from:roly.juarez@gmail.com}")
    private String fromEmail;

    @Async("asyncExecutor")
    @Override
    public void sendHtml(String to, String subject, String htmlBody) {
        if (!enabled) {
            log.debug("Email deshabilitado, se omite envío a {}", to);
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email enviado a {}: {}", to, subject);
        } catch (Exception e) {
            log.error("Error al enviar email a {}: {}", to, e.getMessage());
        }
    }
}
