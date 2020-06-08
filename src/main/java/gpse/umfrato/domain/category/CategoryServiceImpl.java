package gpse.umfrato.domain.category;

import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollRepository;
import gpse.umfrato.domain.user.User;
import gpse.umfrato.web.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    /**
     * Declare a poll repository.
     */
    private final PollRepository pollRepository;
    /**
     * Declare a category repository.
     */
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(final CategoryRepository categoryRepository, final PollRepository pollRepository) {
        this.categoryRepository = categoryRepository;
        this.pollRepository = pollRepository;
    }
    @Override
    public Category createCategory(final String name, final long pollId) {
        final Category category = new Category(name, pollId);
        categoryRepository.save(category);
        pollRepository.findById(pollId).orElseThrow(EntityNotFoundException::new).getCategoryList().add(category);
        return category;
    }

    @Override
    public void deleteCategory(long categoryId) {
        final long pollId = categoryRepository.findById(categoryId).
            orElseThrow(EntityNotFoundException::new).getPollId();

        final long standardCategoryId= pollRepository.findById(pollId).orElseThrow(EntityNotFoundException::new)
            .getCategoryList().get(0).getCategoryId();

        categoryRepository.findById(categoryId).orElseThrow(EntityNotFoundException::new).getQuestionList().stream()
            .forEach(question -> question.setCategoryId(standardCategoryId));

        categoryRepository.deleteById(categoryId);
    }

    @Override
    public void deleteCategoryAndQuestions(long categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public List<Category> getAllCategories(long pollId) {
        final List<Category> categories = categoryRepository.findCategoriesByPollId(pollId);
        /* if (categories.isEmpty()) {
            throw new BadRequestException();
        } */

        return categories;
    }

}
