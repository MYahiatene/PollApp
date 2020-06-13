package gpse.umfrato.domain.question;

import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.category.CategoryRepository;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollRepository;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.web.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    /**
     * Initializes the poll service.
     */
    /*default*/ final PollService pollService;

    /*default*/ final CategoryRepository categoryRepository;

    /**
     * Initializes the poll repository.
     */
    private final PollRepository pollRepository;

    /**
     * Initializes the question repository.
     */
    private final QuestionRepository questionRepository;

    /**
     * This class constructor initializes the poll-, question repository and the poll service.
     *
     * @param pollRepository     the repository for the polls
     * @param questionRepository the repository for the questions
     * @param pollService        the class to work with polls
     * @param categoryRepository the repository for the groups
     */
    @Autowired
    public QuestionServiceImpl(final PollRepository pollRepository, final QuestionRepository questionRepository,
                               final PollService pollService, final CategoryRepository categoryRepository) {
        this.pollRepository = pollRepository;
        this.questionRepository = questionRepository;
        this.pollService = pollService;
        this.categoryRepository = categoryRepository;
    }


    /**
     * This method creates a question for a poll.
     *
     * @param questionMessage the given question
     * @return the question which is created
     */
    @Override
    public Question addQuestion(final String pollId,
                                final String questionMessage,
                                final List<String> answerPossibilities,
                                final String questionType) {
        final Question question = new Question(questionMessage, answerPossibilities, questionType);
        final Category category = categoryRepository.findCategoryByPollId(Long.valueOf(pollId));
        category.getQuestionList().add(question);
        question.setCategoryId(category.getCategoryId());
        questionRepository.save(question);
        categoryRepository.save(category);
        return question;
    }

    /**
     * This method removes a selected question.
     *
     * @param pollId     the id of the poll where the question is setted
     * @param questionId the id of the selectes question
     */
    @Override
    public void removeQuestion(final String pollId, final String questionId) {
        try {
            questionRepository.deleteById(Long.valueOf(questionId));

        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method returns the requested question.
     *
     * @param questionId the id of the requested question
     * @return the requested question
     */
    @Override
    public Question getQuestion(final Long questionId) {

        return questionRepository.findById(questionId).orElseThrow(EntityNotFoundException::new);
    }

    /**
     * This method returns all questions from a poll.
     *
     * @return all questions from a poll
     */
    @Override
    public List<Question> getAllQuestions(final long pollId) {
        final Poll poll = pollRepository.findById(pollId).orElseThrow(EntityNotFoundException::new);
        final List<Category> categories = poll.getCategoryList();
        final List<Question> allQuestions = new ArrayList<>();

        //for (final Category g : categories) {
            allQuestions.addAll(categories.get(0).getQuestionList());
        //}
        System.out.println(allQuestions.size());
        if (allQuestions.isEmpty()) {
            throw new BadRequestException();
        }

        return allQuestions;
    }

    @Override
    public List<Question> getQuestionsFromCategory(final long categoryId) {
        return questionRepository.findQuestionByCategoryId(categoryId);
    }


}
