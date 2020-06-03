package gpse.umfrato.web;

import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.answer.AnswerService;
import gpse.umfrato.domain.cmd.AnswerCmd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;

@RequestMapping(value = "/api", method = RequestMethod.GET)
@RestController
@CrossOrigin
public class AnswerController {

    private final AnswerService answerService;


    /**
     * This class constructor initializes the objects.
     *
     * @param answerService      the answer service
     */
    @Autowired
    public AnswerController(final AnswerService answerService) {
        this.answerService = answerService;

    }

    /**
     * This method adds an answer.
     *
     * @param answerCmd the answerCmd which has the requested data
     */
    @PostMapping("/poll/pollId:1/addanswer") // {pollId:\d+}
    public void addAnswer(final @RequestBody AnswerCmd answerCmd) {
        answerService.giveAnswer(answerCmd.getUsername(), answerCmd.getPollId(),
            answerCmd.getQuestionId(), answerCmd.getAnswerList());
    }

    /**
     * This method deletes an answer.
     * @param answerCmd the needed information is saved in the answerCmd object
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
