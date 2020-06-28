package gpse.umfrato.web;

import gpse.umfrato.domain.ConsistencyQuestion.ConsistencyQuestion;
import gpse.umfrato.domain.ConsistencyQuestion.ConsistencyQuestionService;
import gpse.umfrato.domain.cmd.ConsistencyQuestionCmd;
import gpse.umfrato.domain.cmd.PollCmd;
import gpse.umfrato.domain.participationlinks.ParticipationLinkService;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Logger;

@RequestMapping(value = "/api", method = RequestMethod.GET)
@RestController
@CrossOrigin
public class PollController {
    static final Logger LOGGER = Logger.getLogger("PollController");
    private final PollService pollService;
    private final ParticipationLinkService participationLinkService;
    private final ConsistencyQuestionService consistencyQuestionService;

    /**
     * This class constructor initializes the poll service.
     *
     * @param pollService              the poll service to work with the poll
     * @param participationLinkService
     */
    @Autowired
    public PollController(final PollService pollService, final ParticipationLinkService participationLinkService,
                          final ConsistencyQuestionService consistencyQuestionService) {
        this.pollService = pollService;
        this.participationLinkService = participationLinkService;
        this.consistencyQuestionService = consistencyQuestionService;
    }

    /**
     * This method creates the poll with the given settings from the PollCreation page.
     *
     * @param pollCmd
     * @return String with PollID or Error
     */
    @PostMapping(value = "/createpoll", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('Admin')")
    public Long createPoll(final @RequestBody PollCmd pollCmd) {
        try {
            final Poll poll = pollService.createPoll(pollCmd.getCmdPoll());
            participationLinkService.createParticipationLink(poll.getPollId(), "allUsers");
            return poll.getPollId();
        } catch (BadRequestException | MalformedURLException e) {
            return -1L;
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

    @PostMapping("/activatePoll/{pollId:\\d+}")
    public Integer activatePoll(final @PathVariable Long pollId) {
        return pollService.activatePoll(pollId);
    }

    /**
     * This method returns the poll (questions, settings etc).
     *
     * @param link represents the pollLink
     * @return a poll with the pollId given in the PathVariable
     */
    @GetMapping("/participant/{link}")
    public Poll getPoll(@PathVariable("link") final String link) {
        final Poll poll = pollService.getPoll(participationLinkService
            .getPollIdFromParticipationLink(link));
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

    @GetMapping("relevantpolls")
    public List<Poll> getNewestPolls()
    {
        return pollService.getLastEditedPolls();
    }

    @GetMapping("/getonepoll")
    public Poll getPoll(final @RequestParam long pollId) {
        return pollService.getPoll(pollId);
    }

    @PostMapping("/poll/{pollId:\\d+}/addcq")
    public ConsistencyQuestion addConsistencyQuestion(final @PathVariable long pollId, final @RequestBody ConsistencyQuestionCmd consistencyQuestionCmd)
    {
        consistencyQuestionCmd.setPollId(pollId);
        return consistencyQuestionService.createConsistencyQuestion(consistencyQuestionCmd);
    }

    @GetMapping("/poll/{pollId:\\d+}/consistencyquestions")
    public List<ConsistencyQuestion> getConsistencyQuestions(final @PathVariable long pollId)
    {
        return consistencyQuestionService.getAllConsistencyQuestions(pollId);
    }

    @GetMapping("/poll/{pollId:\\d+}/consistencyquestionnumber")
    public Integer getConsistencyQuestionCount(final @PathVariable long pollId)
    {
        return consistencyQuestionService.getAllConsistencyQuestions(pollId).size();
    }

    @GetMapping("/poll/{pollId:\\d+}/consistencyquestions/{question1Id:\\d+}/{question2Id:\\d+}")
    public List<ConsistencyQuestion> getConsistencyQuestions(final @PathVariable long pollId,final @PathVariable long question1Id,final @PathVariable long question2Id)
    {
        return consistencyQuestionService.getAllConsistencyQuestions(question1Id,question2Id);
    }

    @PostMapping("/poll/editcq/{cqId:\\d+}")
    public void editConsistencyQuestion(final @PathVariable long cqId, final @RequestBody ConsistencyQuestionCmd consistencyQuestionCmd)
    {
        consistencyQuestionService.editConsistencyQuestion(cqId,consistencyQuestionCmd);
    }

    @PostMapping("/poll/delcq/{cqId:\\d+}")
    public void addConsistencyQuestion(final @PathVariable long cqId)
    {
        consistencyQuestionService.deleteConsistencyQuestion(cqId);
    }
}

