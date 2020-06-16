package gpse.umfrato.domain.category;

import gpse.umfrato.domain.poll.PollRepository;
import gpse.umfrato.domain.question.QuestionRepository;
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

    private final QuestionRepository questionRepository;

    @Autowired
    public CategoryServiceImpl(final CategoryRepository categoryRepository, final PollRepository pollRepository,
                               final QuestionRepository questionRepository) {
        this.categoryRepository = categoryRepository;
        this.pollRepository = pollRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public Category createCategory(final String name, final long pollId) {
        final Category category = new Category(name, pollId);
        category.setPollId(pollId);
        pollRepository.findById(pollId).orElseThrow(EntityNotFoundException::new).getCategoryList().add(category);
        categoryRepository.save(category);
        return category;
    }

    @Override
    public String deleteCategory(Long categoryId, String questionState) {
        final long pollId = categoryRepository.findById(categoryId).
            orElseThrow(EntityNotFoundException::new).getPollId();

        if (questionState.equals("1")) {
            final long standardCategoryId = pollRepository.findById(pollId).orElseThrow(EntityNotFoundException::new)
                .getCategoryList().get(0).getCategoryId();

            categoryRepository.findById(categoryId).orElseThrow(EntityNotFoundException::new).getQuestionList()
                .forEach(question -> question.setCategoryId(standardCategoryId));
        }
        if (questionState.equals("2")) {
            categoryRepository.findById(categoryId).orElseThrow(EntityNotFoundException::new).getQuestionList()
                .forEach(question -> questionRepository.deleteById(question.getQuestionId()));
            categoryRepository.findById(categoryId).orElseThrow(EntityNotFoundException::new).
                setQuestionList(new ArrayList<>());
        }
        pollRepository.findById(pollId).orElseThrow(EntityNotFoundException::new).getCategoryList().
            remove(categoryRepository.findById(categoryId).orElseThrow(EntityNotFoundException::new));
        categoryRepository.deleteById(categoryId);

        return ("Kategorie gelöscht");
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
