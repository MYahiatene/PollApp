package gpse.umfrato.web;

import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.cmd.CategoryCmd;
import gpse.umfrato.domain.cmd.QuestionCategoryChangeCmd;
import gpse.umfrato.domain.cmd.QuestionCmd;
import gpse.umfrato.domain.question.Question;
import gpse.umfrato.domain.question.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

import javax.transaction.Transactional;
import java.util.List;

@RequestMapping(value = "/api", method = RequestMethod.GET)
@RestController
@CrossOrigin(origins = "http://127.0.0.1:8080")
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
    @PostMapping("/addquestion")
    public Question addQuestion(final @RequestBody QuestionCmd questionCmd) {
        return questionService.addQuestion(questionCmd);
    }

    /**
     * This method deletes a question.
     *
     * @param questionCmd     the Cmd of the question
     */
    @PutMapping("/removeQuestion")
    public void deleteQuestion(final @RequestBody QuestionCmd questionCmd) {
        questionService.removeQuestion(String.valueOf(questionCmd.getCategoryId()), String.valueOf(questionCmd.getQuestionId()));
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

    @PutMapping("/editquestion")
    public Question editQuestion(final @RequestBody QuestionCmd questionCmd) {
        return questionService.editQuestion(questionCmd);
    }

    @PostMapping("/changequestioncategory")
    public Question changequestioncategory(final @RequestBody QuestionCategoryChangeCmd questionCategoryChangeCmd) {
        return questionService.changeCategory(questionCategoryChangeCmd.getQuestionId(),
            questionCategoryChangeCmd.getOldCategoryId(), questionCategoryChangeCmd.getNewCategoryId());
    }
}
