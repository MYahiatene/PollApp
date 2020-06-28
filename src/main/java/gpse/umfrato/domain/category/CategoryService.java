package gpse.umfrato.domain.category;

import java.util.List;

public interface CategoryService {
    /**
     * This method creates a category.
     * @param name the name of the category
     * @param pollId the id of the poll to which the category is added
     * @return returns the category as an object
     */
    Category createCategory(final String name, final long pollId);

    /**
     * This method deletes a category.
     * @param categoryId the id of the category
     * @param questionState the question state of the category
     * @return returns a confirmation String
     */
    String deleteCategory(final Long categoryId, final String questionState);

    /**
     * This method returns all the categories belonging to one poll id.
     * @param pollId the poll id
     * @return returns all the categories as a list
     */
    List<Category> getAllCategories(final long pollId);

    /**
     * This method edits the name of a category.
     * @param categoryId the id of the category
     * @param name the new name of the category
     */
    void editCategory(final long categoryId, final String name);
}
