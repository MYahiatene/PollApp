package gpse.umfrato.web;

import gpse.umfrato.domain.answer.AnswerService;
import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.cmd.AnswerCmd;
import gpse.umfrato.domain.cmd.CategoryCmd;
import gpse.umfrato.domain.question.QuestionRepository;
import gpse.umfrato.domain.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api", method = RequestMethod.GET)
@RestController
@CrossOrigin
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(final CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/poll/{pollId:\\d+}/addcategory")
    public void addCategory(final @RequestBody CategoryCmd categoryCmd) {
        categoryService.addCategory(categoryCmd.getName(), Long.valueOf(categoryCmd.getPollId()));
    }
}
