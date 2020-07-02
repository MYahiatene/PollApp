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

            URL invitationLink = participationLinkService.createParticipationLink();

            for (String mail : mailCmd.getMailList()) {

                if (mailCmd.getAnonymityStatus().equals("3")) { // 3 = nicht anonym

                    invitationLink = participationLinkService.createParticipationLink();
                    participationLinkService.saveParticipationLink(mailCmd.getPollId(), mail,
                        invitationLink.toString());

                } else if (mailCmd.getAnonymityStatus().equals("2")) { // 2 = teilanonym, Username ist unique Key

                    invitationLink = participationLinkService.createParticipationLink();
                    participationLinkService.saveParticipationLink(mailCmd.getPollId(), invitationLink.toString(),
                        invitationLink.toString());

                } else { // 1 = anonym, es ist jedes mal der selbe Link

                    participationLinkService.saveParticipationLink(mailCmd.getPollId(), invitationLink.toString(),
                        invitationLink.toString());

                }

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
