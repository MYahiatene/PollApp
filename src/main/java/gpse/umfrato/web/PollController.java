package gpse.umfrato.web;


import gpse.umfrato.domain.poll.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/createpoll")
    public String createPoll() {
        try {
            pollService.createPoll("Poll vom 28.10.1900");
            return "Poll created!";
        } catch (Exception e) {
            return "Poll creation failed!";
        }
    }




}
