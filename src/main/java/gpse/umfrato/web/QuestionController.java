package gpse.umfrato.web;

import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.cmd.CategoryCmd;
import gpse.umfrato.domain.cmd.QuestionCmd;
import gpse.umfrato.domain.question.Question;
import gpse.umfrato.domain.question.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@RequestMapping(value = "/api", method = RequestMethod.GET)
@RestController
@CrossOrigin(origins = "http://127.0.0.1:8080")
public class QuestionController {

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
    @PostMapping("/addquestion")
    public Question addQuestion(final @RequestBody QuestionCmd questionCmd) {
        return questionService.addQuestion(questionCmd.getPollId(), questionCmd.getQuestionMessage(),
            questionCmd.getAnswerPossibilities(), questionCmd.getQuestionType());
    }

    /**
     * This method deletes a question.
     *
     * @param questionCmd has the poll id
     */
    @PostMapping("/removeQuestion")
    public void deleteQuestion(final @RequestBody QuestionCmd questionCmd) {
        questionService.removeQuestion(questionCmd.getPollId(), questionCmd.getQuestionId());
    }

    /**
     * This method gets a requested question.
     *
     * @param questionCmd has the poll id of the question
     * @return returns the requested question
     */
    @GetMapping("/getOneQuestion")
    public Question getQuestion(final @RequestBody QuestionCmd questionCmd) {

        return questionService.getQuestion(Long.valueOf(questionCmd.getPollId()));
    }

    @GetMapping("/getallquestions")
    public List<Question> getAllQuestions(final @RequestParam long categoryId) {

        return questionService.getAllQuestions(categoryId);
    }
}
