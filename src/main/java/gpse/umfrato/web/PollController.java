package gpse.umfrato.web;

import gpse.umfrato.domain.consistencyquestion.ConsistencyQuestion;
import gpse.umfrato.domain.consistencyquestion.ConsistencyQuestionService;
import gpse.umfrato.domain.cmd.ConsistencyQuestionCmd;
import gpse.umfrato.domain.cmd.PollCmd;
import gpse.umfrato.domain.participationlinks.ParticipationLinkService;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.poll.SmallPoll;
import gpse.umfrato.domain.pollresult.PollResultService;
import gpse.umfrato.domain.question.Question;
import gpse.umfrato.domain.question.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;

/**
 * The Poll controller used to process poll specific requests.
 */
@RequestMapping(value = "/api", method = RequestMethod.GET)
@RestController
@CrossOrigin
public class PollController {
    /* default */ static final Logger LOGGER = Logger.getLogger("PollController");
    private final PollService pollService;
    private final PollResultService pollResultService;
    private final ParticipationLinkService participationLinkService;
    private final ConsistencyQuestionService consistencyQuestionService;
    private final QuestionService questionService;

    /**
     * This class constructor initializes the poll service and the participationLink service.
     *
     * @param pollService              the poll service to work with the poll.
     * @param participationLinkService the participationLink service to work with.
     */
    @Autowired
    public PollController(final PollService pollService, final PollResultService pollResultService,
                          final ParticipationLinkService participationLinkService,
                          final ConsistencyQuestionService consistencyQuestionService, QuestionService questionService) {
        this.pollService = pollService;
        this.pollResultService = pollResultService;
        this.participationLinkService = participationLinkService;
        this.consistencyQuestionService = consistencyQuestionService;
        this.questionService = questionService;

    }

    /**
     * This method creates the poll with the given settings from the PollCreation page.
     *
     * @param pollCmd takes the necessary pollCmd object.
     * @return returns string with PollID or Error
     */
    @PostMapping(value = "/createpoll", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('Admin', 'Creator')")
    public Long createPoll(final @RequestBody PollCmd pollCmd) {
        try {
            LOGGER.info("isActivated: " + pollCmd.isActivated());
            LOGGER.info("pollCmd: " + pollCmd);
            final Poll poll = pollService.createPoll(pollCmd.getCmdPoll());
            if (poll.getAnonymityStatus().equals("1")) {
                final String link = participationLinkService.createParticipationLink().toString();
                participationLinkService.saveParticipationLink(poll.getPollId(), "allUsers", link);
            }
            return poll.getPollId();
        } catch (BadRequestException | MalformedURLException e) {
            return -1L;
        }
    }

    /**
     * This method returns a list with information about every poll.
     *
     * @return a list with information about every poll.
     */
    @GetMapping("/poll")
    public List<SmallPoll> getSmallPolls() {
        List<SmallPoll> polls = new ArrayList<>();
        for(Poll p: pollService.getAllPolls())
        {
            polls.add(new SmallPoll(p,pollResultService,participationLinkService));
        }
        return polls;
    }

    /**
     * This method creates the poll with the given settings from the PollCreation page.
     *
     * @param pollCmd
     * @return String with PollID or Error
     */
    @PostMapping(value = "/createcopypoll", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('Admin')")
    public String createCopyPoll(final @RequestBody PollCmd pollCmd) {
        try {
            LOGGER.info("pollCmd: " + pollCmd);
            final Poll poll = pollService.createCopyPoll(pollCmd.getCmdPoll());
            // TODO: participationLinkService.createParticipationLink(poll.getPollId(), "allUsers");
            LOGGER.info(poll.toString());
            return poll.getPollId().toString();
        } catch (BadRequestException e) { // | MalformedURLException e) {
            return null;
        }
    }

    /**
     * This method returns a list with all polls.
     *
     * @return a list with all polls.
     */
    @GetMapping("/bigPoll")
    public List<Poll> getPolls() {
        return pollService.getAllPolls();
    }

    /**
     * This method is used to set the activation status of a poll.
     *
     * @param pollId takes the pollId of the specific poll.
     * @return returns the integer value of the activation status.
     */
    @PostMapping("/activatePoll/{pollId:\\d+}")
    public Integer activatePoll(final @PathVariable Long pollId) {
        return pollService.activatePoll(pollId);
    }

    /**
     * This method returns the poll (questions, settings etc).
     *
     * @param link represents the pollLink.
     * @return a poll with the pollId given in the PathVariable.
     */
    @GetMapping("/participant/{link}")
    public Poll getPoll(@PathVariable("link") final String link) {
        final Poll poll = pollService.getPoll(participationLinkService
            .getPollIdFromParticipationLink(link));
        if (poll == null) {
            throw new BadRequestException();
        } else {
            return poll;
        }
    }

    /**
     * This method returns the username or creates an anonymous one if the poll is anonymous.
     *
     * @param pollCmd represents the given Poll.
     * @return returns the specific username.
     */
    // @GetMapping("/getUsername")
    @RequestMapping(value = "/getUsername", method = RequestMethod.POST)
    public String getUsername(final @RequestBody PollCmd pollCmd) {
        if (pollCmd.getAnonymityStatus().equals("1")) {
            return pollService.createAnonymousUsername();
        } else {
            return "Nina"; //TODO: Nina muss raus!
        }
    }

    @GetMapping("relevantpolls")
    public List<SmallPoll> getNewestPolls()
    {
        List<SmallPoll> relevantPolls = new ArrayList<>();
        for(Poll p: pollService.getLastEditedPolls())
        {
            relevantPolls.add(new SmallPoll(p,pollResultService,participationLinkService));
        }
        return relevantPolls;
    }

    /**
     * This method returns one poll object.
     *
     * @param pollId the poll object will be identified by the poll id.
     * @return returns the poll object.
     */
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
    @PostMapping("/poll/{pollId:\\d+}/question/{questionId:\\d+}/addAnswerPossibility")
    public Poll addAnswerPossibility(final @PathVariable Long pollId, final @PathVariable Long questionId, final @RequestBody String answer) {
        String tmp = answer.substring(answer.indexOf(':'));
        Question question = questionService.getQuestion(questionId);
        questionService.setNewAnswer(question, tmp.substring(2,tmp.length()-2));
        return pollService.getPoll(pollId);
    }
    /**
     * This method edits the name of the poll.
     *
     * @param pollCmd the Cmd includes the id and the new name of the poll.
     */
    @PreAuthorize("hasAnyAuthority('Admin','Creator')")
    @PutMapping("/editpollname")
    public void editPollName(final @RequestBody PollCmd pollCmd) {
        pollService.editPollName(Long.parseLong(pollCmd.getPollId()), pollCmd.getPollName());
    }

    /**
     * This method deletes a poll.
     *
     * @param pollCmd the Cmd includes the id of the poll which will be deleted
     * @return returns a confirmation String
     */
    @PreAuthorize("hasAnyAuthority('Admin','Creator')")
    @PutMapping("/deletepoll")
    public String deletePoll(final @RequestBody PollCmd pollCmd) {
        return pollService.deletePoll(pollCmd.getPollId());
    }

    @GetMapping("/getPollName")
    public String getPollName(final @RequestParam long pollId) {
        LOGGER.info("PollID: " + String.valueOf(pollId));
        return pollService.getPoll(pollId).getPollName();
    }

    @GetMapping("/getAnonType")
    public String getAnonType(final @RequestParam long pollId) {
        LOGGER.info("PollID: " + String.valueOf(pollId));
        LOGGER.info("anonymity status: " + pollService.getPoll(pollId).getAnonymityStatus());
        switch(pollService.getPoll(pollId).getAnonymityStatus()) {
            case "0": return "Anonym";
            case "1": return "Anonym";
            case "2": return "Teilanonym";
            case "3": return "Nichtanonym";
        }
        return "Anonym";
    }
    @GetMapping("/getActDate")
    public String getActDate(final @RequestParam long pollId) {
        LOGGER.info("PollID: " + String.valueOf(pollId));
        final Calendar date = pollService.getPoll(pollId).getActivatedDate();
        return pollService.parseDate(date);
    }

    @GetMapping("/getDeactDate")
    public String getDeactDate(final @RequestParam long pollId) {
        LOGGER.info("PollID: " + String.valueOf(pollId));
        final Calendar date = pollService.getPoll(pollId).getDeactivatedDate();
        return pollService.parseDate(date);
    }

}

