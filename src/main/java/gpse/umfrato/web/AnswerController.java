package gpse.umfrato.web;

import gpse.umfrato.domain.answer.AnswerService;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.question.QuestionRepository;
import gpse.umfrato.domain.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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


    @PostMapping("/poll/{pollId:\\d+}/question/{questionId:\\d+}/addanswer")
    public void addAnswer(
        @PathVariable("questionId") final String questionId) {
        String message = "Jero ist geil!!!";
        answerService.addAnswer(message, questionId);
    }

    @PostMapping("/poll/{pollId:\\d+}/question/{questionId:\\d+}/deleteanswer/{answerId:\\d+}")
    public void deleteAnswer(
        @PathVariable("questionId") final String questionId,
        @PathVariable("answerId") final String answerId) {
        answerService.deleteAnswer(questionId, answerId);
    }
}
