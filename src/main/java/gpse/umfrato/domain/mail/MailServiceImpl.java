package gpse.umfrato.domain.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MailServiceImpl implements MailService {
    private List<String> mailAdresses;
    private JavaMailSender javaMailSender;
    private static final String MAILADRESS = "gpseteam5.1@gmail.com";

    @Autowired
    public MailServiceImpl(final JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void setMailAdresses(final List<String> mailList) {
        this.mailAdresses = mailList;
    }

    @Override
    public List<String> getMailAdresses() {
        return mailAdresses;
    }

    @Override
    public void sendMail(final String mailSubject, final String mailMessage, final String emailAdress) {
        final SimpleMailMessage message = new SimpleMailMessage();
        final String mailText = mailMessage;

        message.setFrom(MAILADRESS);
        message.setTo(emailAdress);
        message.setSubject(mailSubject);
        message.setText(mailText);

        this.javaMailSender.send(message);
    }
}
