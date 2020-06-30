package gpse.umfrato.web;

import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.cmd.CategoryCmd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The category controller used to process category specific requests.
 */
@RequestMapping("/api")
@RestController
@CrossOrigin
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * The constructor is used inject the category service.
     *
     * @param categoryService
     */
    @Autowired
    public CategoryController(final CategoryService categoryService) {
        this.categoryService = categoryService;
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
}
