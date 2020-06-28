package gpse.umfrato.web;

import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.cmd.CategoryCmd;
import gpse.umfrato.domain.cmd.PollCmd;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.question.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;

@RequestMapping(value = "/api")
@RestController
@CrossOrigin
public class CategoryController {

    static final Logger LOGGER = Logger.getLogger("CategoryController");
    private static final String TEST = "Test";
    private final CategoryService categoryService;
    private final QuestionService questionService;
    // nur vorübergehend:
    private final PollService pollService;

    @Autowired
    public CategoryController(final CategoryService categoryService, final QuestionService questionService,
                              final PollService pollService) {
        this.categoryService = categoryService;
        this.questionService = questionService;
        this.pollService = pollService;
    }

    @PostMapping("/addcategory")
    public Category addCategory(final @RequestBody CategoryCmd categoryCmd) {
        return categoryService.createCategory(categoryCmd.getName(), Long.parseLong(categoryCmd.getPollId()));
    }

    @GetMapping("/getallcategories")
    public List<Category> getCategories(final @RequestParam long pollId) {
        return categoryService.getAllCategories(pollId);
    }

    /**
     * Deletes a category in the data base.
     *
     * @param categoryCmd the Cmd of category
     */
    @PreAuthorize("hasAuthority('Admin')")
    @PutMapping("/deletecategory")
    public String deleteCategory(final @RequestBody CategoryCmd categoryCmd) {
        categoryService.deleteCategory(Long.parseLong(categoryCmd.getCategoryId()));
        return TEST;
    }

    @PutMapping("/editcategory")
    public void editCategory(final @RequestBody CategoryCmd categoryCmd) {
        categoryService.editCategory(Long.valueOf(categoryCmd.getCategoryId()), categoryCmd.getName());
    }

    /**
     * Deletes a category in the data base.
     *
     * @param categoryCmd the Cmd of Category
     */
    @PreAuthorize("hasAuthority('Admin')")
    @PutMapping("/deletecategoryandquestions")
    public String deleteCategoryAndQuestions(final @RequestBody CategoryCmd categoryCmd) {
        categoryService.deleteCategoryAndQuestions(Long.valueOf(categoryCmd.getCategoryId()));
        return TEST;
    }

    @PostMapping(value = "/copycategories/{oldPollId:\\d+}/{newPollId:\\d+}")
    @PreAuthorize("hasAuthority('Admin')")
    public String copyCategories(final @PathVariable Long oldPollId, final @PathVariable long newPollId) {
        try {
            final List<Category> categories = categoryService.getAllCategories(Long.valueOf(oldPollId));
            final ListIterator<Category> iterator = categories.listIterator();
            while(iterator.hasNext()) {
                final Category oldCategory = iterator.next();
                final Category newCategory = categoryService.createCategory(oldCategory.getCategoryName(), newPollId);
                questionService.copyQuestions(newCategory.getCategoryId(), newPollId, oldCategory.getQuestionList());
            }
            return "Categories created: " + categories.toString();
        } catch (BadRequestException e) {
            return "Category creation failed!";
        }
    }
}
