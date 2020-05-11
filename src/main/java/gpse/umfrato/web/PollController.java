package gpse.umfrato.web;


import gpse.umfrato.domain.cmd.PollCmd;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/PollCreation")
    public String createPoll(final @RequestBody PollCmd pollCmd) {
        try {
            pollService.createPoll(pollCmd.getPollname(), pollCmd.getPollcreator(), pollCmd.getPollCreatedAt(),
                pollCmd.getLastEditAt(), pollCmd.getActivatedAt(), pollCmd.getDeactivatedAt(),
                pollCmd.getAnonymityStatus(), pollCmd.getPollStatus(), new ArrayList<>()); //arraylist vorübergehend
                // TODO: Add new attributes here
            return "Poll created!";
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

    /**
     * This method returns a selected poll.
     *
     * param pollCmd has the id of the poll
     * @return a selected poll
     */
    @GetMapping("/participant") // TODO: /poll/{id:\d+} wieder einfügen
    public Poll getPoll(/*@PathVariable("id") final String id final @RequestBody PollCmd pollCmd */) {

        return pollService.getPoll("1"); // TODO: pollCmd.getId()
    }


}
