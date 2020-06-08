package gpse.umfrato.web;

import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.cmd.CategoryCmd;
import gpse.umfrato.domain.cmd.DeleteUserCmd;
import gpse.umfrato.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(value = "/api")
@RestController
@CrossOrigin
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(final CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/addcategory")
    public Category addCategory(final @RequestBody CategoryCmd categoryCmd) {
        return categoryService.createCategory(categoryCmd.getName(), Long.parseLong(categoryCmd.getPollId()));
    }

    @PostMapping("/categories")
    public List<Category> getCategories(final @RequestBody CategoryCmd categoryCmd) {
        return categoryService.getAllCategories(Long.parseLong(categoryCmd.getPollId()));
    }

    /**
     * Deletes a category in the data base
     */
    @PreAuthorize("hasAuthority('Admin')")
    @PutMapping("/deletecategory")
    public String deleteCategory(final @RequestBody CategoryCmd categoryCmd) {
        categoryService.deleteCategory(Long.valueOf(categoryCmd.getCategoryId()));
        return "Test";
    }
    /**
     * Deletes a category in the data base
     */
    @PreAuthorize("hasAuthority('Admin')")
    @PutMapping("/deletecategoryandquestions")
    public String deleteCategoryAndQuestions(final @RequestBody CategoryCmd categoryCmd) {
        categoryService.deleteCategoryAndQuestions(Long.valueOf(categoryCmd.getCategoryId()));
        return "Test";
    }
}
