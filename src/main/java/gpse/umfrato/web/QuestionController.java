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

    private static final Logger LOGGER = Logger.getLogger("QuestionController");
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
    public Long addQuestion(@PathVariable("id") final Long id, final @RequestBody QuestionCmd questionCmd) {
        // wollen wir lieber die PathVariable id benutzen oder die Id aus der QuestionCmd holen?
        return questionService.addQuestion(id, questionCmd.getQuestion()).getQuestionId();
    }

    /**
     * This method deletes a question.
     *
     * @param pollId     has the poll id
     * @param questionId has the question id
     */
    @PostMapping("/poll/{pollId:\\d+}/removequestion/{questionId:\\d+}")
    public void deleteQuestion(@PathVariable("pollId") final String pollId, @PathVariable("questionId")
    final String questionId) {
        questionService.removeQuestion(pollId, questionId);
    }

    /**
     * This method gets a requested question.
     *
     * @return returns the requested question
     */
    @GetMapping("/poll/{pollId:\\d+}/getquestion/{questionId:\\d+}")
    public Question getQuestion(@PathVariable("questionId") final Long id) {
        return questionService.getQuestion(id);
    }
}
