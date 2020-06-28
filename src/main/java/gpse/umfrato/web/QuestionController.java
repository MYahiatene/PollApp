package gpse.umfrato.web;

import gpse.umfrato.domain.cmd.QuestionCategoryChangeCmd;
import gpse.umfrato.domain.cmd.QuestionCmd;
import gpse.umfrato.domain.question.Question;
import gpse.umfrato.domain.question.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

import java.util.List;

@RequestMapping(value = "/api", method = RequestMethod.GET)
@RestController
@CrossOrigin(origins = "http://127.0.0.1:8080")
public class QuestionController {

    /* default */ static final Logger LOGGER = Logger.getLogger("QuestionController");
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
    @PreAuthorize("hasAnyAuthority('Admin','Creator', 'Editor')")
    @PostMapping("/addquestion")
    public Question addQuestion(final @RequestBody QuestionCmd questionCmd) {
        return questionService.addQuestion(questionCmd);
    }

    /**
     * This method deletes a question.
     *
     * @param questionCmd the Cmd of the question
     */
    @PreAuthorize("hasAnyAuthority('Admin','Creator', 'Editor')")
    @PutMapping("/removequestion")
    public void deleteQuestion(final @RequestBody QuestionCmd questionCmd) {
        questionService.removeQuestion(String.valueOf(questionCmd.getCategoryId()),
            String.valueOf(questionCmd.getQuestionId()));
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

    @PreAuthorize("hasAnyAuthority('Admin','Creator', 'Editor')")
    @PutMapping("/editquestion")
    public Question editQuestion(final @RequestBody QuestionCmd questionCmd) {
        return questionService.editQuestion(questionCmd);
    }

    @PreAuthorize("hasAnyAuthority('Admin','Creator', 'Editor')")
    @PostMapping("/changequestioncategory")
    public Question changeQuestionCategory(final @RequestBody QuestionCategoryChangeCmd questionCategoryChangeCmd) {
        return questionService.changeCategory(Long.parseLong(questionCategoryChangeCmd.getQuestionId()),
            Long.parseLong(questionCategoryChangeCmd.getNewCategoryId()),
            Long.parseLong(questionCategoryChangeCmd.getNewIndex()));
    }
}
