package gpse.umfrato.web;

import gpse.umfrato.domain.cmd.AnswerCmd;
import gpse.umfrato.domain.pollresult.PollResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

/**
 * The PollResult controller used to process poll result specific requests.
 */
@RequestMapping(value = "/api", method = RequestMethod.GET)
@RestController
@CrossOrigin
public class PollResultController {

    private static final Logger LOGGER = Logger.getLogger("PollResultController");
    private final PollResultService pollResultService;

    /**
     * This class constructor initializes the pollResult service.
     *
     * @param pollResultService the pollResult service
     */
    @Autowired
    public PollResultController(final PollResultService pollResultService) {
        this.pollResultService = pollResultService;

    }

    /**
     * This method is used to return the pollResult of a specific user.
     *
     * @param answerCmd takes the necessary answerCmd object.
     */
    @PreAuthorize("hasAnyAuthority('Admin','Creator')")
    @RequestMapping(value = "/getPollResult", method = RequestMethod.POST) //"/pollResult/{usernameId:\\d+}"
    public void pollResultByUsername(final @RequestBody AnswerCmd answerCmd) {
        LOGGER.info("Hi from the PollResultController"); // hier gibt er es schon nicht aus
        LOGGER.info(answerCmd.toString());
        pollResultService.getPollResult(Long.parseLong(answerCmd.getPollId()), answerCmd.getUsername());
    }
}
