package gpse.umfrato.web;

import gpse.umfrato.domain.answer.poll.PollService;
import gpse.umfrato.domain.answer.question.Question;
import gpse.umfrato.domain.answer.question.QuestionService;
import gpse.umfrato.domain.answer.user.User;
import gpse.umfrato.domain.answer.user.UserService;
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
    private static final Logger LOGGER = Logger.getLogger("QuestionController");

    @Autowired
    public QuestionController(final QuestionService questionService, final UserService userService,final PollService pollService) {
        this.questionService = questionService;
        this.userService = userService;
        this.pollService= pollService;
    }

    @PostMapping("/createquestion")
    public String createQuestion() {
        try{
        Question question = questionService.createQuestion("Ist Jero der coolste? :-D",1L);
            return "Die Frage: \""+question.getQuestion() +"\" wurde erstellt!";
        }
        catch (Exception e) {
            return e.getCause().toString();
        }
    }

    @PostMapping("/createanswer")
    public String createAnswer() {
        try {
            User user = (User) userService.loadUserByUsername("Richie");
            questionService.createAnswer("Ja", user, "Richard", 1L);
            return "Answer created sucessfully by " + user.getUsername() + "!";
        } catch (Exception e) {
            return e.getCause().toString();
        }
    }
}
