package gpse.umfrato.web;

import gpse.umfrato.domain.cmd.MailCmd;
import gpse.umfrato.domain.participationlinks.ParticipationLink;
import gpse.umfrato.domain.participationlinks.ParticipationLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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

            String invitationLink;

            for (final String mail : mailCmd.getMailList()) {

                if (mailCmd.getAnonymityStatus().equals("3")) { // 3 = nicht anonym

                    invitationLink = participationLinkService.createParticipationLink().toString();
                    participationLinkService.saveParticipationLink(mailCmd.getPollId(), mail,
                        invitationLink.toString());

                } else if (mailCmd.getAnonymityStatus().equals("2")) { // 2 = teilanonym, Username ist unique Key

                    invitationLink = participationLinkService.createParticipationLink().toString();
                    participationLinkService.saveParticipationLink(mailCmd.getPollId(), mail, invitationLink.toString()
                    );

                } else { // 1 = anonym, es ist jedes mal der selbe Link

                    final List<ParticipationLink> participationLinks = participationLinkService.getAllParticipationLinks(mailCmd.getPollId());
                    invitationLink = participationLinks.get(0).getGeneratedParticipationLink();
                    participationLinkService.saveParticipationLink(mailCmd.getPollId(), mail, invitationLink
                    );

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

    @PostMapping(value = "/sendRemindEmail")
    public String sendRemindEmail(final @RequestBody MailCmd mailCmd) {
        try {

            final List<ParticipationLink> participationList = participationLinkService.getAllParticipationLinks(mailCmd.getPollId());
            final SimpleMailMessage message = new SimpleMailMessage();

            for (final ParticipationLink participationLink : participationList) {
                if (participationLink.getUsername().contains("@") && participationLink.getUsername().contains(".")) {
                    String mailText = mailCmd.getEmailMessage();
                    mailText = mailText.replace("{link}", participationLink.getGeneratedParticipationLink());
                    message.setTo(participationLink.getUsername());
                    message.setSubject(mailCmd.getEmailSubject());
                    message.setText(mailText);

                    this.mailSender.send(message);
                }
            }

            return "Email sent.";

        } catch (MailException e) {

            return "Email sending failed.";

        }
    }
}
