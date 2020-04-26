package gpse.umfrato.web;

import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.answer.AnswerService;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.question.Question;
import gpse.umfrato.domain.question.QuestionService;
import gpse.umfrato.domain.user.User;
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
    private static final Logger LOGGER = Logger.getLogger("QuestionController");

    @Autowired
    public QuestionController(final QuestionService questionService, final UserService userService,
                              final PollService pollService, final AnswerService answerService) {
        this.questionService = questionService;
        this.userService = userService;
        this.pollService = pollService;
        this.answerService = answerService;
    }

    @PostMapping("/createquestion")
    public String createQuestion() {
        try {
            this.question = questionService.createQuestion("Ist Jero der coolste? :-D", 1L);
            return "Die Frage: \"" + question.getQuestion() + "\" wurde erstellt!";
        } catch (Exception e) {
            return e.getCause().toString();
        }
    }

    @PostMapping("/createanswer")
    public String createAnswer() {
        try {
            User user = (User) userService.loadUserByUsername("Uncle_Bob");
            Poll poll = pollService.createPoll("Random Poll");

            answerService.createAnswer("Ja", user, "Richard", question.getId());
            return "Answer created sucessfully by " + user.getUsername() + "!";
        } catch (Exception e) {
            return e.getCause().toString();
        }
    }
}
