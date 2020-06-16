package gpse.umfrato.domain.category;

import java.util.List;

public interface CategoryService {
    Category createCategory(final String name, final long pollId);
    String deleteCategory(final Long categoryId, final String questionState);
    List<Category> getAllCategories(final long pollId);
    void editCategory(final long categoryId, final String name);
}
