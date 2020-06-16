package gpse.umfrato.web;

import gpse.umfrato.domain.cmd.CategoryCmd;
import gpse.umfrato.domain.cmd.PollCmd;
import gpse.umfrato.domain.participationlinks.ParticipationLinkService;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Logger;

@RequestMapping(value = "/api", method = RequestMethod.GET)
@RestController
@CrossOrigin
public class PollController {
    /* default */ static final Logger LOGGER = Logger.getLogger("PollController");
    private final PollService pollService;
    private final ParticipationLinkService participationLinkService;

    /**
     * This class constructor initializes the poll service.
     *
     * @param pollService              the poll service to work with the poll
     * @param participationLinkService
     */
    @Autowired
    public PollController(final PollService pollService, final ParticipationLinkService participationLinkService) {
        this.pollService = pollService;
        this.participationLinkService = participationLinkService;
    }

    /**
     * This method creates the poll with the given settings from the PollCreation page.
     *
     * @param pollCmd
     * @return String with PollID or Error
     */
    @PostMapping(value = "/createpoll", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('Admin')")
    public String createPoll(final @RequestBody PollCmd pollCmd) {
        try {
            final Poll poll = pollService.createPoll(pollCmd.getCmdPoll());
            participationLinkService.createParticipationLink(poll.getPollId(), "allUsers");
            return "Poll created! with id: " + poll.getPollId().toString();
        } catch (BadRequestException | MalformedURLException e) {
            return "Poll creation failed!";
        }
    }

    /**
     * This method returns a list with all polls.
     *
     * @return a list with all polls.
     */
    @GetMapping("/poll")
    public List<Poll> getPolls() {
        return pollService.getAllPolls();
    }

    @PostMapping("/activatePoll/{pollId:\\d+}")
    public Integer activatePoll(final @PathVariable Long pollId) {
        return pollService.activatePoll(pollId);
    }

    /**
     * This method returns the poll (questions, settings etc).
     *
     * @param link represents the pollLink
     * @return a poll with the pollId given in the PathVariable
     */
    @GetMapping("/participant/{link}")
    public Poll getPoll(@PathVariable("link") final String link) {
        final Poll poll = pollService.getPoll(String.valueOf(participationLinkService
            .getPollIdFromParticipationLink(link)));
        if (poll != null) {
            return poll;
        } else {
            throw new BadRequestException();
        }
    }

    /**
     * This method returns the username or creates an anonym one if the poll is anonym.
     *
     * @param pollCmd represents the given Poll
     * @return a username
     */
    // @GetMapping("/getUsername")
    @RequestMapping(value = "/getUsername", method = RequestMethod.POST)
    public String getUsername(final @RequestBody PollCmd pollCmd) {
        if (pollCmd.getAnonymityStatus().equals("1")) {
            return pollService.createAnonymUsername();
        } else {
            return "Nina";
        }
    }

    @GetMapping("/getonepoll")
    public Poll getPoll(final @RequestParam long pollId) {
        return pollService.getPoll(String.valueOf(pollId));
    }

    @PutMapping("/editpollname")
    public void editPollName(final @RequestBody PollCmd pollCmd) {
        pollService.editPollName(Long.parseLong(pollCmd.getPollId()), pollCmd.getPollName());
    }

}

