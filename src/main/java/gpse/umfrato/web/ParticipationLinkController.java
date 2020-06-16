package gpse.umfrato.web;

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

    @Autowired
    public ParticipationLinkController(final ParticipationLinkService participationLinkService) {
        this.participationLinkService = participationLinkService;
    }

    @GetMapping("/participationLinks")
    public List<ParticipationLink> getParticipationLinks() {
        /*if (participationLinkService.getAllParticipationLinks(pollCmd.getId()).isEmpty()) {
            throw new BadRequestException();
        } else {*/
            return participationLinkService.getAllParticipationLinks();
        //}
    }
}
