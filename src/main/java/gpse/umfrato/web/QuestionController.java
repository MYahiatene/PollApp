package gpse.umfrato.web;

import gpse.umfrato.domain.answer.AnswerService;
import gpse.umfrato.domain.poll.PollRepository;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.question.Question;
import gpse.umfrato.domain.question.QuestionService;
import gpse.umfrato.domain.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RequestMapping(value = "/api", method = RequestMethod.GET)
@RestController
@CrossOrigin
public class QuestionController {
    private QuestionService questionService;
    private UserService userService;
    private PollService pollService;
    private AnswerService answerService;
    private Question question;
    private PollRepository pollRepository;
    private static final Logger LOGGER = Logger.getLogger("QuestionController");

    @Autowired
    public QuestionController(final QuestionService questionService, final UserService userService,
                              final PollService pollService, final AnswerService answerService,
                              PollRepository pollRepository) {
        this.questionService = questionService;
        this.userService = userService;
        this.pollService = pollService;
        this.answerService = answerService;
        this.pollRepository = pollRepository;
    }

    @PostMapping("/poll/{id:\\d+}/addquestion")
    public void addQuestion(@PathVariable("id") final String id) {
        String message = "Jero ist geil";
        questionService.addQuestion(message, id);
    }

    @PostMapping("/poll/{pollId:\\d+}/removequestion/{questionId:\\d+}")
    public void deleteQuestion(
        @PathVariable("pollId") final String pollId,
        @PathVariable("questionId") final String questionId) {
        questionService.removeQuestion(pollId, questionId);
    }
}
