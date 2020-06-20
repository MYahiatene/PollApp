package gpse.umfrato.domain.category;

import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
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
        category.setPollId(pollId); //unnötig?
        pollRepository.findById(pollId).orElseThrow(EntityNotFoundException::new).getCategoryList().add(category);
        categoryRepository.save(category);
        return category;
    }

    @Override
    public void deleteCategory(final long categoryId) {
        final long pollId = categoryRepository.findById(categoryId).
            orElseThrow(EntityNotFoundException::new).getPollId();

        final long standardCategoryId = pollRepository.findById(pollId).orElseThrow(EntityNotFoundException::new)
            .getCategoryList().get(0).getCategoryId();

        categoryRepository.findById(categoryId).orElseThrow(EntityNotFoundException::new).getQuestionList()
            .forEach(question -> question.setCategoryId(standardCategoryId));

        pollRepository.findById(pollId).orElseThrow(EntityNotFoundException::new).getCategoryList().
            remove(categoryRepository.findById(categoryId).orElseThrow(EntityNotFoundException::new));
        categoryRepository.deleteById(categoryId);

    }

    @Override
    public void deleteCategoryAndQuestions(final long categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public List<Category> getAllCategories(final long pollId) {
        return categoryRepository.findCategoriesByPollId(pollId);
    }

    @Override
    public void editCategory(final long categoryId, final String name) {
        final Category category = categoryRepository.getOne(categoryId);
        category.setCategoryName(name);
        categoryRepository.save(category);
    }

}
