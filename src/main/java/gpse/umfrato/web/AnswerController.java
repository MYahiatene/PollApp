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

    final UserService userService;
    final QuestionRepository questionRepository;
    private final AnswerService answerService;


    /**
     * This class constructor initializes the objects.
     *
     * @param userService        the user service

     * @param answerService      the answer service
     * @param questionRepository the question repository
     */
    @Autowired
    public AnswerController(final UserService userService,
                            final AnswerService answerService, final QuestionRepository questionRepository) {
        this.answerService = answerService;
        this.userService = userService;
        this.questionRepository = questionRepository;

    }

    /**
     * This method adds an answer.
     *
     * @param answerCmd the answerCmd which has the requested data
     */
    @PostMapping("/poll/{pollId:\\d+}/addanswer")
    public void addAnswer(final @RequestBody AnswerCmd answerCmd) {
        answerService.giveAnswer(answerCmd.getUsername(),answerCmd.getPollId(),
            answerCmd.getQuestionId(),answerCmd.getAnswerList());
    }

    /**
     * This method deletes an answer.
     */
    @PostMapping("/question/{questionId:\\d+}/deleteanswer")
    public void deleteAnswer(final @RequestBody AnswerCmd answerCmd) {
        answerService.deleteAnswer(answerCmd.getAnswerId());
    }

    /**
     * This method returns a requested answer.
     *
     * @param answerCmd gives the answerId of the requested answer
     * @return the requested answer
     */
    //todo: url anpassen
    @GetMapping("/poll/getAnswer")
    public List<Answer> getAnswerFromOneQuestion(final @RequestBody AnswerCmd answerCmd) {
        return answerService.getAnswerFromOneQuestion(Long.valueOf(answerCmd.getQuestionId()));
    }
    //todo: fix this function in controller and service impl
    @GetMapping("/poll/getAnswerFromUser")
    public List<String> getAnswersFromPollByUser(final @RequestBody AnswerCmd answerCmd) {
        return answerService.getAllAnswersFromPollByUser(Long.valueOf(answerCmd.getPollId()), answerCmd.getUsername());
    }
}
