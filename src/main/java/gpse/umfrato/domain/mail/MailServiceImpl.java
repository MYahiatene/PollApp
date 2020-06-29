package gpse.umfrato.domain.mail;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MailServiceImpl implements MailService {
    List<String> mailAdresses;

    @Override
    public void setMailAdresses(List<String> mailList) {
        this.mailAdresses = mailList;
    }

    @Override
    public List<String> getMailAdresses() {
        return mailAdresses;
    }
}
