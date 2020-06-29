package gpse.umfrato.web;

import gpse.umfrato.domain.mail.MailService;
import gpse.umfrato.domain.participationlinks.ParticipationLink;
import gpse.umfrato.domain.participationlinks.ParticipationLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RequestMapping(value = "/api", method = RequestMethod.GET)
@RestController
@CrossOrigin
public class ParticipationLinkController {
    /* default */ static final Logger LOGGER = Logger.getLogger("ParticipationLinkController");
    private final ParticipationLinkService participationLinkService;
    private final MailService mailService;

    @Autowired
    public ParticipationLinkController(final ParticipationLinkService participationLinkService,
                                       final MailService mailService) {
        this.participationLinkService = participationLinkService;
        this.mailService = mailService;
    }

    @GetMapping("/participationLinks")
    public List<ParticipationLink> getParticipationLinks() {
        mailService.getMailAdresses();
        return participationLinkService.getAllParticipationLinks();
    }
}
