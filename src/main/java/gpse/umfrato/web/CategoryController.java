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
import java.util.logging.Logger;

/**
 * The category controller used to process category specific requests.
 */
@RequestMapping("/api")
@RestController
@CrossOrigin
public class CategoryController {

    /* default */ static final Logger LOGGER = Logger.getLogger("CategoryController");
    private final CategoryService categoryService;
    private final QuestionService questionService;

    /**
     * The constructor is used inject the category service.
     *
     * @param categoryService
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
    @PreAuthorize("hasAnyAuthority('Admin', 'Creator', 'Editor')")
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
    @PreAuthorize("hasAnyAuthority('Admin', 'Creator', 'Editor')")
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
    @PreAuthorize("hasAnyAuthority('Admin', 'Creator', 'Editor')")
    @PutMapping("/editcategory")
    public void editCategory(final @RequestBody CategoryCmd categoryCmd) {
        categoryService.editCategory(categoryCmd.getCategoryId(), categoryCmd.getCategoryName());
    }

    @PostMapping(value = "/copycategories/{oldPollId:\\d+}/{newPollId:\\d+}")
    @PreAuthorize("hasAnyAuthority('Admin', 'Creator', 'Editor')")
    public String copyCategories(final @PathVariable Long oldPollId, final @PathVariable long newPollId) {
        try {
            LOGGER.info("start copy categories, old pollId:" + oldPollId + " newPollId: " + newPollId);
            final List<Category> categories = categoryService.getAllCategories(Long.valueOf(oldPollId));
            final ListIterator<Category> iterator = categories.listIterator();
            while(iterator.hasNext()) {
                final Category oldCategory = iterator.next();
                LOGGER.info("old category: " + oldCategory.toString());
                final Category newCategory = categoryService.createCategory(oldCategory.getCategoryName(), newPollId);
                LOGGER.info("new category: " + newCategory.toString());
                questionService.copyQuestions(newCategory.getCategoryId(), newPollId, oldCategory.getQuestionList());
                LOGGER.info("new category: " + newCategory.toString());
            }
            return "Categories created: " + categories.toString();
        } catch (BadRequestException e) {
            return "Category creation failed!";
        }
    }
}
