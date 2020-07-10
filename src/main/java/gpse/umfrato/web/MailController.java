package gpse.umfrato.web;

import gpse.umfrato.domain.cmd.MailCmd;
import gpse.umfrato.domain.mail.MailService;
import gpse.umfrato.domain.participationlinks.ParticipationLink;
import gpse.umfrato.domain.participationlinks.ParticipationLinkService;
import gpse.umfrato.domain.pollresult.PollResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/api")
@RestController
@CrossOrigin
public class MailController {


    static final int DEFAULT_PORT = 8080;

    private MailService mailService;
    //private JavaMailSender mailSender;
    private ParticipationLinkService participationLinkService;
    private PollResultService pollResultService;

    @Autowired
    public MailController(final MailService mailService,//final JavaMailSender javaMailSender,
                          final ParticipationLinkService participationLinkService, final PollResultService pollResultService) {
        this.mailService = mailService;
        //this.mailSender = javaMailSender;
        this.participationLinkService = participationLinkService;
        this.pollResultService = pollResultService;
    }

    @PostMapping("/sendEmail")
    public String sendEmail(final @RequestBody MailCmd mailCmd) {

        try {

            String invitationLink = new String();

            for (final String mail : mailCmd.getMailList()) {

                if (mailCmd.getAnonymityStatus().equals("3")) { // 3 = nicht anonym

                    invitationLink = participationLinkService.createParticipationLink().toString();
                    participationLinkService.saveParticipationLink(mailCmd.getPollId(), mail, invitationLink);

                } else if (mailCmd.getAnonymityStatus().equals("2")) { // 2 = teilanonym, Username ist unique Key

                    invitationLink = participationLinkService.createParticipationLink().toString();
                    participationLinkService.saveParticipationLink(mailCmd.getPollId(), mail, invitationLink);

                }

                String mailText = mailCmd.getEmailMessage();
                mailText = mailText.replace("{link}", invitationLink);

                this.mailService.sendMail(mailCmd.getEmailSubject(), mailText, mail);

            }

            return "Email sent.";

        } catch (MailException e) {

            return "Email sending failed.";

        } catch (MalformedURLException e) {

            return "Creating unique value failed.";

        }

    }

    @PostMapping("/sendRemindEmail")
    public String sendRemindEmail(final @RequestBody MailCmd mailCmd) {

        try {

            final List<ParticipationLink> allParticipants = participationLinkService.getAllParticipationLinks
                (mailCmd.getPollId());

            final List<ParticipationLink> participationList;
            if (mailCmd.getNotificateParticipants() == 1) { // Teilnehmer, die Poll bereits abgeschlossen
                participationList = new ArrayList<>();
                for (ParticipationLink participationLink : allParticipants) {
                    if (pollResultService.getParticipated(participationLink.getUsername(), mailCmd.getPollId())) {
                        participationList.add(participationLink);
                    }
                }
            } else if (mailCmd.getNotificateParticipants() == 2) { // Teilnehmer, die Poll nicht abgeschlossen
                participationList = new ArrayList<>();
                for (ParticipationLink participationLink : allParticipants) {
                    if (!(pollResultService.getParticipated(participationLink.getUsername(), mailCmd.getPollId()))) {
                        participationList.add(participationLink);
                    }
                }
            } else { // Alle Teilnehmer
                participationList = participationLinkService.getAllParticipationLinks(mailCmd.getPollId());
            }

            for (final ParticipationLink participationLink : participationList) {
                if (participationLink.getUsername().contains("@") && participationLink.getUsername().contains(".")) {
                    String mailText = mailCmd.getEmailMessage();
                    mailText = mailText.replace("{link}", participationLink.getGeneratedParticipationLink());

                    this.mailService.sendMail(mailCmd.getEmailSubject(), mailText, participationLink.getUsername());

                }
            }

            return "Email sent.";

        } catch (MailException e) {

            return "Email sending failed.";

        }
    }
}
