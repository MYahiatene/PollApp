package gpse.umfrato.domain.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MailServiceImpl implements MailService {
    List<String> mailAdresses;
    private JavaMailSender javaMailSender;

    @Autowired
    public MailServiceImpl(final JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void setMailAdresses(List<String> mailList) {
        this.mailAdresses = mailList;
    }

    @Override
    public List<String> getMailAdresses() {
        return mailAdresses;
    }

    @Override
    public void sendMail(String mailSubject, String mailMessage, String emailAdress) {
        final SimpleMailMessage message = new SimpleMailMessage();
        String mailText = mailMessage;
        message.setTo(emailAdress);
        message.setSubject(mailSubject);
        message.setText(mailText);

        this.javaMailSender.send(message);
    }
}
