package gpse.umfrato.web;

import gpse.umfrato.domain.cmd.PollCmd;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RequestMapping(value = "/api", method = RequestMethod.GET)
@RestController
@CrossOrigin
public class PollController {
    private static final Logger LOGGER = Logger.getLogger("PollController");
    private final PollService pollService;

    /**
     * This class constructor initializes the poll service.
     *
     * @param pollService the poll service to work with the poll
     */
    @Autowired
    public PollController(final PollService pollService) {
        this.pollService = pollService;
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
            return "Poll created! with id: " + poll.getPollId().toString();
        } catch (BadRequestException e) {
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

    /**
     * This method returns the poll (questions, settings etc).
     *
     * @param id repreents the pollId
     * @return a poll with the pollId given in the PathVariable
     */
    @GetMapping("/participant/{id:\\d+}")
    public Poll getPoll(@PathVariable("id") final String id) {
        Poll poll = pollService.getPoll(id);
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
    public Poll getPoll(@RequestParam long pollId) {
        return pollService.getPoll(String.valueOf(pollId));
    }


}

