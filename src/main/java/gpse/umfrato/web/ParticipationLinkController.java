package gpse.umfrato.web;

import gpse.umfrato.domain.participationlinks.ParticipationLink;
import gpse.umfrato.domain.participationlinks.ParticipationLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

/**
 * The ParticipationLink controller used to process participationLink specific requests.
 */
@RequestMapping(value = "/api", method = RequestMethod.GET)
@RestController
@CrossOrigin
public class ParticipationLinkController {
    private final ParticipationLinkService participationLinkService;

    @Autowired
    public ParticipationLinkController(final ParticipationLinkService participationLinkService) {
        this.participationLinkService = participationLinkService;
    }

    @GetMapping("/participationLinks")
    public List<ParticipationLink> getParticipationLinks() {
        return participationLinkService.getAllParticipationLinks();
    }
}
