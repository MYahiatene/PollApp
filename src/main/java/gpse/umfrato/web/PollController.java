package gpse.umfrato.web;

import gpse.umfrato.domain.cmd.PollCmd;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(value = "/api", method = RequestMethod.GET)
@RestController
@CrossOrigin
public class PollController {

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

    @PostMapping(value = "/createpoll", produces = MediaType.APPLICATION_JSON_VALUE)
    public String createPoll(final @RequestBody PollCmd pollCmd) {
        try {
            pollService.createPoll(pollCmd.getCmdPoll());
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
     * @param pollCmd has the id of the poll
     * @return a selected poll
     */
    @GetMapping("/poll/{id:\\d+}")
    public Poll getPoll(final @RequestBody PollCmd pollCmd) {

        return pollService.getPoll(pollCmd.getId());

    }


}
