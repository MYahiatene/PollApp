package gpse.umfrato.web;


import gpse.umfrato.domain.cmd.PollCmd;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.question.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
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
    public String createPoll(final @RequestBody PollCmd pollCmd) {
        try {
            Poll poll = pollService.createPoll(pollCmd.getCmdPoll());
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
    public Poll getPoll(final @RequestBody PollCmd pollCmd) {
        return pollService.getPoll(pollCmd.getId());
    }


    /**
     * This method returns the poll (questions, settings etc).
     *
     * @return a selected poll
     */
    @GetMapping("/participant")
    public Poll getParticipant() {
        try {
            LOGGER.info("1");
            Poll poll = pollService.getPoll("1");
            LOGGER.info(poll.toString());
            return poll;
            // return pollService.getPoll("2");
        } catch(EntityNotFoundException e) {
            return null;
        }

    }

}
