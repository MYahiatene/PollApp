package gpse.umfrato.domain.category;

import gpse.umfrato.domain.poll.Poll;
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

    /**
     * This method creates a category.
     * @param name the name of the category
     * @param pollId the id of the poll to which the category is added
     * @return returns the category as an object
     */
    @Override
    public Category createCategory(final String name, final long pollId) {
        final Category category = new Category(name, pollId);
        category.setPollId(pollId); //unnötig?
        pollRepository.getOne(pollId).getCategoryList().add(category);
        categoryRepository.save(category);
        return category;
    }

    /**
     * This method deletes a category.
     * @param categoryId the id of the category
     * @param questionState the question state of the category
     * @return returns a confirmation String
     */
    @Override
    public String deleteCategory(final Long categoryId, final String questionState) {
        final long pollId = categoryRepository.findById(categoryId).
            orElseThrow(EntityNotFoundException::new).getPollId();

        if (questionState.equals("1")) {
            final long standardCategoryId = pollRepository.findById(pollId).orElseThrow(EntityNotFoundException::new)
                .getCategoryList().get(0).getCategoryId();
            final List<Long> tmpList = new ArrayList<>();
            categoryRepository.getOne(categoryId).getQuestionList()
                .forEach(question -> {
                    question.setCategoryId(standardCategoryId);
                    tmpList.add(question.getQuestionId());
                });
            tmpList.forEach(id -> categoryRepository.findById(standardCategoryId).orElseThrow(EntityNotFoundException::
                new).getQuestionList().add(questionRepository.getOne(id)));
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

        return "Kategorie gelöscht";
    }

    /**
     * This method returns all the categories belonging to one poll id.
     * @param pollId the poll id
     * @return returns all the categories as a list
     */
    @Override
    public List<Category> getAllCategories(final long pollId) {
        return categoryRepository.findCategoriesByPollId(pollId);
    }

    /**
     * This method edits the name of a category.
     * @param categoryId the id of the category
     * @param name the new name of the category
     */
    @Override
    public void editCategory(final long categoryId, final String name) {
        final Category category = categoryRepository.getOne(categoryId);
        category.setCategoryName(name);
        categoryRepository.save(category);
    }

}
