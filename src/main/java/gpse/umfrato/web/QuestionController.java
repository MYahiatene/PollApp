package gpse.umfrato.web;

import gpse.umfrato.domain.answer.AnswerService;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollRepository;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.question.Question;
import gpse.umfrato.domain.question.QuestionService;
import gpse.umfrato.domain.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
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
        this.pollRepository=pollRepository;
    }

    @GetMapping("/createquestion")
    public String createQuestion() {
        try {
           Poll poll= pollRepository.findById(1L).orElseThrow(() -> new EntityNotFoundException());
           Question question=new Question("Ist Jero der coolste? :-D",poll);
            poll.addQuestion(question);
            pollRepository.save(poll);
            return "Die Frage: \"" + question.getQuestion() + "\" wurde erstellt!";
        } catch (Exception e) {
            return e.getCause().toString();
        }
    }


}
