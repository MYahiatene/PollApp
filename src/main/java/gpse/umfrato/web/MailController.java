package gpse.umfrato.web;

import gpse.umfrato.domain.cmd.MailCmd;
import gpse.umfrato.domain.participationlinks.ParticipationLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

@RequestMapping("/api")
@RestController
@CrossOrigin
public class MailController {


    /* default */ static final int DEFAULT_PORT = 8080;

    private JavaMailSender mailSender;
    private ParticipationLinkService participationLinkService;

    @Autowired
    public MailController(final JavaMailSender javaMailSender, final ParticipationLinkService participationLinkService) {
        this.mailSender = javaMailSender;
        this.participationLinkService = participationLinkService;
    }

    @PostMapping(value = "/sendEmail")
    public String sendEmail(final @RequestBody MailCmd mailCmd) {

        try {

            final SimpleMailMessage message = new SimpleMailMessage();
            for (String mail : mailCmd.getMailList()) {
                final UUID uuid = UUID.randomUUID();
                final String urlUuid = "/" + uuid.toString();
                final URL invitationLink = new URL("http", "localhost", DEFAULT_PORT, urlUuid);

                String mailText = mailCmd.getEmailMessage();
                mailText = mailText.replace("{link}", invitationLink.toString());
                message.setTo(mail);
                message.setSubject(mailCmd.getEmailSubject());
                message.setText(mailText);


                this.mailSender.send(message);

            }

            return "Email sent.";

        } catch (MailException e) {

            return "Email sending failed.";

        } catch (MalformedURLException e) {

            return "Creating unique value failed.";

        }

    }

}
