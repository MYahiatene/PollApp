package gpse.umfrato.web;

import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.cmd.CategoryCmd;
import gpse.umfrato.domain.question.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ListIterator;

/**
 * The category controller used to process category specific requests.
 */
@RequestMapping("/api")
@RestController
@CrossOrigin
public class CategoryController {

    private static final String HAS_ANY_AUTHORITY = "hasAnyAuthority('Admin', 'Creator', 'Editor')";
    private final CategoryService categoryService;
    private final QuestionService questionService;

    /**
     * The constructor is used inject the category service.
     *
     * @param categoryService
     * @param questionService
     */
    @Autowired
    public CategoryController(final CategoryService categoryService, final QuestionService questionService) {
        this.categoryService = categoryService;
        this.questionService = questionService;
    }

    /**
     * This method adds a category.
     *
     * @param categoryCmd the Cmd includes the name and the poll id of the new category.
     * @return returns the new category object.
     */
    @PreAuthorize(HAS_ANY_AUTHORITY)
    @PostMapping("/addcategory")
    public Category addCategory(final @RequestBody CategoryCmd categoryCmd) {
        return categoryService.createCategory(categoryCmd.getCategoryName(), categoryCmd.getPollId());
    }

    /**
     * This method returns all the categories belonging to one poll.
     *
     * @param pollId the id of the poll.
     * @return returns all the categories.
     */
    @GetMapping("/getallcategories")
    public List<Category> getCategories(final @RequestParam long pollId) {
        return categoryService.getAllCategories(pollId);
    }

    /**
     * Deletes a category in the data base.
     *
     * @param categoryCmd the Cmd of category.
     */
    @PreAuthorize(HAS_ANY_AUTHORITY)
    @PutMapping("/deletecategory")
    public String deleteCategory(final @RequestBody CategoryCmd categoryCmd) {
        return categoryService.deleteCategory(categoryCmd.getCategoryId(),
            categoryCmd.getQuestionState().toString());
    }

    /**
     * This method changes the name of the category.
     *
     * @param categoryCmd the Cmd includes the required id and the new name of the catgegory.
     */
    @PreAuthorize(HAS_ANY_AUTHORITY)
    @PutMapping("/editcategory")
    public void editCategory(final @RequestBody CategoryCmd categoryCmd) {
        categoryService.editCategory(categoryCmd.getCategoryId(), categoryCmd.getCategoryName());
    }

    /**
     * Copies Copies all categories and their questions from one Poll to a new one.
     * @param oldPollId
     * @param newPollId
     * @return
     */
    @PostMapping("/copycategories/{oldPollId:\\d+}/{newPollId:\\d+}")
    @PreAuthorize(HAS_ANY_AUTHORITY)
    public String copyCategories(final @PathVariable Long oldPollId, final @PathVariable long newPollId) {
        try {
            final List<Category> categories = categoryService.getAllCategories(Long.valueOf(oldPollId));
            final ListIterator<Category> iterator = categories.listIterator();
            while (iterator.hasNext()) {
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
