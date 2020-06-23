package gpse.umfrato.web;

import gpse.umfrato.domain.cmd.AnswerCmd;
import gpse.umfrato.domain.pollresult.PollResult;
import gpse.umfrato.domain.pollresult.PollResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RequestMapping(value = "/api", method = RequestMethod.GET)
@RestController
@CrossOrigin
public class PollResultController {

    private static final Logger LOGGER = Logger.getLogger("PollResultController");
    private final PollResultService pollResultService;

    /**
     * This class constructor initializes the objects.
     * @param pollResultService      the answer service
     */
    @Autowired
    public PollResultController(final PollResultService pollResultService) {
        this.pollResultService = pollResultService;

    }
    @RequestMapping(value = "/getPollResult", method = RequestMethod.POST) //"/pollResult/{usernameId:\\d+}"
    public PollResult pollResultByUsername(final @RequestBody AnswerCmd answerCmd) {
        LOGGER.info("Hi from the PollResultController");
        LOGGER.info(answerCmd.toString());
        return pollResultService.getPollResult(Long.parseLong(answerCmd.getPollId()), answerCmd.getUsername());
    }
}
