package gpse.umfrato.domain.category;

public interface CategoryService {
    Category createCategory(final String name, final long pollId);
    void deleteCategory(final long categoryId);
    void deleteCategoryAndQuestions(final long categoryId);
}
