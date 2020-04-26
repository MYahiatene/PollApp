package gpse.umfrato.web;

import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.answer.AnswerService;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.question.Question;
import gpse.umfrato.domain.question.QuestionRepository;
import gpse.umfrato.domain.user.User;
import gpse.umfrato.domain.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;

@RequestMapping(value = "/api", method = RequestMethod.GET)
@RestController
@CrossOrigin
public class AnswerController {

    private final UserService userService;
    private final PollService pollService;
    private final AnswerService answerService;
    private final QuestionRepository questionRepository;

    @Autowired
    public AnswerController(final UserService userService, final PollService pollService,
                            final AnswerService answerService, final QuestionRepository questionRepository) {
        this.answerService = answerService;
        this.pollService = pollService;
        this.userService = userService;
        this.questionRepository = questionRepository;

    }


    @PostMapping("/createanswer")
    public String createAnswer() {
        try {
            User user = (User) userService.loadUserByUsername("Uncle_Bob");
            Question question= questionRepository.findById(1L).orElseThrow(()-> new EntityNotFoundException());
            question.addAnswer(new Answer("Ja",user, "Richard", question));
            questionRepository.save(question);
            return "Answer created sucessfully by " + user.getUsername() + "!";
        } catch (Exception e) {
            return e.getCause().toString();
        }
    }
}
