package bo.capital.tec.pet.common.email;

public interface EmailService {

    void sendHtml(String to, String subject, String htmlBody);
}
