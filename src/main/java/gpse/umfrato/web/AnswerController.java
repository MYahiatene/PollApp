package gpse.umfrato.web;

import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.answer.AnswerService;
import gpse.umfrato.domain.cmd.AnswerCmd;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.question.QuestionRepository;
import gpse.umfrato.domain.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        /*@PathVariable("questionId") final String questionId*/@RequestBody AnswerCmd answerCmd) {
        String message = "Jero ist geil!!!";
        answerService.addAnswer(answerCmd.getAnswer(), answerCmd.getQuestionId(), answerCmd.getAnswerType(), answerCmd.getUsername());
    }

    @PostMapping("/poll/{pollId:\\d+}/question/{questionId:\\d+}/deleteanswer/{answerId:\\d+}")
    public void deleteAnswer(
        @PathVariable("questionId") final String questionId,
        @PathVariable("answerId") final String answerId) {
        answerService.deleteAnswer(questionId, answerId);
    }

    @GetMapping("/poll/{pollId:\\d+}/question/{questionId:\\d+}/getanswer/{answerId:\\d+}")
    public Answer getAnswer(@RequestBody AnswerCmd answerCmd) {
        return answerService.getAnswer(Long.valueOf(answerCmd.getAnswerId()));
    }

    @GetMapping("/poll/{pollId:\\d+}/question/{questionId:\\d+}/getanswers}")
    public List<Answer> getAnswers(@RequestBody AnswerCmd answerCmd) {
        return answerService.getAllAnswers(answerCmd.getQuestionId());
    }
}
