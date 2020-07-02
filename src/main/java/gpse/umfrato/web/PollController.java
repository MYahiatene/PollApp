package gpse.umfrato.web;

import gpse.umfrato.domain.consistencyquestion.ConsistencyQuestion;
import gpse.umfrato.domain.consistencyquestion.ConsistencyQuestionService;
import gpse.umfrato.domain.cmd.ConsistencyQuestionCmd;
import gpse.umfrato.domain.cmd.PollCmd;
import gpse.umfrato.domain.participationlinks.ParticipationLinkService;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.question.Question;
import gpse.umfrato.domain.question.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Logger;

/**
 * The Poll controller used to process poll specific requests.
 */
@RequestMapping(value = "/api", method = RequestMethod.GET)
@RestController
@CrossOrigin
public class PollController {
    private final PollService pollService;
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
    public PollController(final PollService pollService, final ParticipationLinkService participationLinkService,
                          final ConsistencyQuestionService consistencyQuestionService, QuestionService questionService) {
        this.pollService = pollService;
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
            final Poll poll = pollService.createPoll(pollCmd.getCmdPoll());
            if (poll.getAnonymityStatus().equals("1")) {
                participationLinkService.createParticipationLink();
            }
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
    public List<Poll> getNewestPolls()
    {
        return pollService.getLastEditedPolls();
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

}

