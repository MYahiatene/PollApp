package gpse.umfrato.web;

import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.cmd.CategoryCmd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(value = "/api")
@RestController
@CrossOrigin
public class CategoryController {

    private static final String TEST = "Test";
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(final CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/addcategory")
    public Category addCategory(final @RequestBody CategoryCmd categoryCmd) {
        return categoryService.createCategory(categoryCmd.getName(), categoryCmd.getPollId());
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
        categoryService.deleteCategory(categoryCmd.getCategoryId());
        return TEST;
    }

    @PutMapping("/editcategory")
    public void editCategory(final @RequestBody CategoryCmd categoryCmd) {
        categoryService.editCategory(categoryCmd.getCategoryId(), categoryCmd.getName());
    }

    /**
     * Deletes a category in the data base.
     *
     * @param categoryCmd the Cmd of Category
     */
    @PreAuthorize("hasAuthority('Admin')")
    @PutMapping("/deletecategoryandquestions")
    public String deleteCategoryAndQuestions(final @RequestBody CategoryCmd categoryCmd) {
        categoryService.deleteCategoryAndQuestions(categoryCmd.getCategoryId());
        return TEST;
    }
}
