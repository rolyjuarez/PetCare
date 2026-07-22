package bo.capital.tec.pet.common.email;

public interface EmailService {
    void sendEmail(String to, String subject, String templateName, java.util.Map<String, Object> variables);
    void sendSimpleEmail(String to, String subject, String body);
}
