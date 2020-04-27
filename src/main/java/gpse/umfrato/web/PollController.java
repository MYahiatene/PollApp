package gpse.umfrato.web;


import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RequestMapping(value = "/api", method = RequestMethod.GET)
@RestController
@CrossOrigin
public class PollController {
    private final PollService pollService;
    private static final Logger LOGGER = Logger.getLogger("PollController");

    @Autowired
    public PollController(final PollService pollService){
        this.pollService =pollService;
    }

    @GetMapping("/createpoll")
    public String createPoll() {
        try {
            pollService.createPoll("Poll vom 28.10.1900");
            return "Poll created!";
        } catch (Exception e) {
            return "Poll creation failed!";
        }
    }

    @GetMapping("/poll")
    public List<Poll> getPolls() {
        if (!pollService.getAllPolls().isEmpty()) {
            return pollService.getAllPolls();
        } else {
            throw new BadRequestException();
        }
    }

    @GetMapping("/poll/{id:\\d+}")
    public Poll getPoll(@PathVariable("id") final String id) {
        if (pollService.getPoll(id).isPresent()) {
            return pollService.getPoll(id).get();
        } else {
            throw new BadRequestException();
        }
    }


}
