package gpse.umfrato.domain.mail;

import java.util.List;

public interface MailService {
    void setMailAdresses(List<String> mailList);

    List<String> getMailAdresses();

    void sendMail(String mailSubject, String mailText, String emailAdress);

}
