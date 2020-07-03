package gpse.umfrato.web;

import gpse.umfrato.domain.cmd.AnswerCmd;
import gpse.umfrato.domain.pollresult.PollResult;
import gpse.umfrato.domain.pollresult.PollResultService;
import org.aspectj.apache.bcel.classfile.Module;
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
    public PollResult pollResultByUsername(final @RequestBody AnswerCmd answerCmd) {
        LOGGER.info("Hi from the PollResultController");
        LOGGER.info(answerCmd.toString());
        return pollResultService.getPollResult(answerCmd.getPollId(), answerCmd.getUsername());
    }
}
