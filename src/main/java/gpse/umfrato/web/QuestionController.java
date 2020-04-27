package gpse.umfrato.web;

import gpse.umfrato.domain.answer.AnswerService;
import gpse.umfrato.domain.cmd.QuestionCmd;
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
    private final QuestionService questionService;
    private final UserService userService;
    private final PollService pollService;
    private final AnswerService answerService;
    private Question question;
    private final PollRepository pollRepository;
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
    public void addQuestion(/*@PathVariable("id") final String id*/ @RequestBody QuestionCmd questionCmd) {
        questionService.addQuestion(questionCmd.getQuestion(), questionCmd.getId());
    }

    @PostMapping("/poll/{pollId:\\d+}/removequestion/{questionId:\\d+}")
    public void deleteQuestion(
        /*@PathVariable("pollId") final String pollId,
        @PathVariable("questionId") final String questionId*/
        @RequestBody QuestionCmd questionCmd) {
        String pollId = questionService.getQuestion(Long.valueOf(questionCmd.getId())).getPoll().getId().toString();
        questionService.removeQuestion(pollId, questionCmd.getId());
    }

    @GetMapping("/poll/{pollId:\\d+}/getquestion/{questionId:\\d+}")
    public Question getQuestion(@RequestBody QuestionCmd questionCmd) {

        return questionService.getQuestion(Long.valueOf(questionCmd.getId()));
    }
}
