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
    final PollService pollService;
    final QuestionRepository questionRepository;
    private final AnswerService answerService;


    /**
     * This class constructor initializes the objects.
     *
     * @param userService        the user service
     * @param pollService        the poll service
     * @param answerService      the answer service
     * @param questionRepository the question repository
     */
    @Autowired
    public AnswerController(final UserService userService, final PollService pollService,
                            final AnswerService answerService, final QuestionRepository questionRepository) {
        this.answerService = answerService;
        this.pollService = pollService;
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
        answerService.giveAnswer(answerCmd.getUsername(), answerCmd.getQuestionId(), answerCmd.getAnswerList());
    }

    /**
     * This method deletes an answer.
     *
     * @param questionId the question id from the question
     * @param answerId   the answer id from the answer
     */
    /*@PostMapping("/poll/{pollId:\\d+}/question/{questionId:\\d+}/deleteanswer/{answerId:\\d+}")
    public void deleteAnswer(
        @PathVariable("questionId") final String questionId,
        @PathVariable("answerId") final String answerId) {
        answerService.deleteAnswer(questionId, answerId);
    }*/

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
}
