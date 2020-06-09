package gpse.umfrato.web;

import gpse.umfrato.domain.cmd.QuestionCmd;
import gpse.umfrato.domain.question.Question;
import gpse.umfrato.domain.question.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RequestMapping(value = "/api", method = RequestMethod.GET)
@RestController
@CrossOrigin //(origins = "http://127.0.0.1:8080")
public class QuestionController {

    static final Logger LOGGER = Logger.getLogger("QuestionController");
    private final QuestionService questionService;

    /**
     * This class constructor initializes the services and repository.
     *
     * @param questionService the question service
     */
    @Autowired
    public QuestionController(final QuestionService questionService) {
        this.questionService = questionService;
    }

    /**
     * This method adds a question.
     *
     * @param questionCmd has the question and the poll id
     */
    @PostMapping("/poll/{id:\\d+}/addquestion")
    public void addQuestion(/*@PathVariable("id") final String id*/ final @RequestBody QuestionCmd questionCmd) {
        questionService.addQuestion(questionCmd.getPollId(), questionCmd.getQuestionMessage(),
            questionCmd.getAnswerPossibilities(), questionCmd.getQuestionType());
    }

    /**
     * This method deletes a question.
     *
     * @param questionCmd has the poll id
     */
    @PostMapping("/poll/{pollId:\\d+}/removequestion/{questionId:\\d+}")
    public void deleteQuestion(
        /*@PathVariable("pollId") final String pollId,
        @PathVariable("questionId") final String questionId*/
        final @RequestBody QuestionCmd questionCmd) {
        final String pollId = questionCmd.getPollId();
        questionService.removeQuestion(pollId, questionCmd.getPollId());
    }

    /**
     * This method gets a requested question.
     *
     * @param questionCmd has the poll id of the question
     * @return returns the requested question
     */
    @GetMapping("/poll/{pollId:\\d+}/getquestion/{questionId:\\d+}")
    public Question getQuestion(final @RequestBody QuestionCmd questionCmd) {

        return questionService.getQuestion(Long.valueOf(questionCmd.getPollId()));
    }
}
