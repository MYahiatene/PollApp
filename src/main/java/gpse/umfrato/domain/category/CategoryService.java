package gpse.umfrato.domain.category;

import gpse.umfrato.domain.question.Question;

import java.util.List;

public interface CategoryService {
    Category createCategory(final String name, final long pollId);
    void deleteCategory(final long categoryId);
    void deleteCategoryAndQuestions(final long categoryId);
    List<Category> getAllCategories(final long pollId);
    void editCategory(final long categoryId, final String name);
}
