package gpse.umfrato.web;


import gpse.umfrato.domain.cmd.PollCmd;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
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
        if (pollService.getAllPolls().isEmpty()) {
            throw new BadRequestException();
        } else {
            return pollService.getAllPolls();
        }
    }

    @GetMapping("/poll/{id:\\d+}")
    public Poll getPoll(final @PathVariable String id) {
        return pollService.getPoll(id);
    }


    /**
     * This method returns the poll (questions, settings etc).
     *
     * @return a selected poll
     */
    @GetMapping("/participant")
    public Poll getParticipant() {
        try {
            return pollService.getPoll("1");
        } catch (EntityNotFoundException e) {
            return null;
        }

    }

}
