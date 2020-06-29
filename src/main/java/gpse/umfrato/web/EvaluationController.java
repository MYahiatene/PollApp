package gpse.umfrato.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import gpse.umfrato.domain.cmd.SessionCmd;
import gpse.umfrato.domain.consistencyquestion.ConsistencyQuestionService;
import gpse.umfrato.domain.evaluation.Session.Session;
import gpse.umfrato.domain.evaluation.Session.SessionService;
import gpse.umfrato.domain.evaluation.Statistics;
import gpse.umfrato.domain.answer.AnswerService;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.cmd.FilterCmd;
import gpse.umfrato.domain.participationlinks.ParticipationLinkService;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.pollresult.PollResultService;
import gpse.umfrato.domain.question.QuestionService;
import gpse.umfrato.domain.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Tuple;
import java.util.*;
import java.util.logging.Logger;

@RequestMapping(value = "/api/evaluation", method = RequestMethod.GET)
@RestController
@CrossOrigin
public class EvaluationController {

    private static final Logger LOGGER = Logger.getLogger("EvaluationController");

    @Autowired
    ObjectMapper objectMapper;

    private final AnswerService answerService;
    private final UserService userService;
    private final QuestionService questionService;
    private final PollService pollService;
    private final PollResultService pollResultService;
    private final CategoryService categoryService;
    private final ParticipationLinkService participationLinkService;
    private final ConsistencyQuestionService consistencyQuestionService;
    private final SessionService sessionService;

    enum Filter {
        ANSWER_FILTER, USER_FILTER
    }


    @Autowired
    public EvaluationController(final AnswerService answerService, final UserService userService,
                                final QuestionService questionService, final PollService pollService,
                                final PollResultService pollResultService, final CategoryService categoryService,
                                final ParticipationLinkService participationLinkService,
                                final ConsistencyQuestionService consistencyQuestionService,
                                final SessionService sessionService) {
        this.answerService = answerService;
        this.userService = userService;
        this.questionService = questionService;
        this.pollService = pollService;
        this.pollResultService = pollResultService;
        this.categoryService = categoryService;
        this.participationLinkService = participationLinkService;
        this.consistencyQuestionService = consistencyQuestionService;
        this.sessionService = sessionService;
    }

    /**
     * generates Data to display in Diagrams pased of of filters provided in input.
     *
     * @param input first filter must be of type DataFilter containing the Poll to analyse
     * @return a string in json format with all the data to display
     */
    @PostMapping("/generateDiagram")
    public String populateDiagram(final @RequestBody List<FilterCmd> input) {
        LOGGER.info(input.toString());
        if (input.isEmpty()) {
            return "?";
        }
        final Statistics calculation = new Statistics(answerService, userService, questionService, pollService, pollResultService, categoryService, consistencyQuestionService, input.get(0));
        calculation.loadFilter(input);
        return calculation.generateDiagram();
    }

    @GetMapping("/getParticipants/{pollId:\\d+}")
    public String getParticipants(final @PathVariable Long pollId) {
        if (pollService.getPoll(pollId).getPollStatus() != 1) {
            return "-";
        }
        if (pollService.getPoll(pollId).getAnonymityStatus().equals("1")) {
            return String.valueOf(pollResultService.getPollResults(pollId).size());
        } else {
            return pollResultService.getPollResults(pollId).size() + " / " + participationLinkService.getAllParticipationLinks(pollId).size();
        }
    }

    @GetMapping("/getParticipantNames/{pollId:\\d+}")
    public List<String> getParticipantNames(final @PathVariable Long pollId) {
        if (pollService.getPoll(pollId).getPollStatus() == 1 && pollService.getPoll(pollId).getAnonymityStatus().equals("2")) {
            final List<String> participantList = new ArrayList<>();
            pollResultService.getPollResults(pollId).forEach(pollResult -> participantList.add(pollResult.getPollTaker()));
            return participantList;
        } else {
            return Collections.singletonList("-");
        }
    }

    @PostMapping("/saveSession")
    public Session saveEvaluation(final @RequestBody SessionCmd sessionCmd) {
        Session session;
        if (sessionCmd.getSessionId() == -1L) {
            session = sessionService.createSession(sessionCmd);
        } else {
            session = sessionService.editSession(sessionCmd);
        }
        return session;
    }

    @GetMapping("/loadSession/{sessionId:\\d+}")
    public Session loadEvaluation(final @PathVariable Long sessionId) {
        return sessionService.getSession(sessionId);
    }

    @GetMapping("/getSessions/{pollId:\\d+}")
    public List<Session> getSessions(final @PathVariable Long pollId) {
        List<Session> sessions = sessionService.getAllSessions(pollId);
        for (Session s: sessions) {
            s.setFilterList(Collections.emptyList());
            s.setDiagramFormat(Collections.emptyList());
        }
        return sessions;
    }
}
