package gpse.umfrato.domain.question;

import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.category.CategoryRepository;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.cmd.QuestionCmd;
import gpse.umfrato.domain.poll.PollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private static final float FIVE = 5.0f;
    private static final float ZERO_DOT_ONE = 0.1f;
    private static final float ZERO = 0.0f;
    private static final float ONE = 1.0f;


    private static final String TEXT_QUESTION = "TextQuestion";
    private static final String RANGE_QUESTION = "RangeQuestion";
    private static final String SLIDER_QUESTION = "SliderQuestion";
    private static final String CHOICE_QUESTION = "ChoiceQuestion";
    private static final String SORT_QUESTION = "SortQuestion";

    /**
     * Initializes the poll service.
     */

    /*default*/ final CategoryRepository categoryRepository;

    /* default */ final CategoryService categoryService;

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
     * @param categoryRepository the repository for the groups
     * @param categoryService    the category service
     */
    @Autowired
    public QuestionServiceImpl(final PollRepository pollRepository, final QuestionRepository questionRepository,
                               final CategoryRepository categoryRepository,
                               final CategoryService categoryService) {
        this.pollRepository = pollRepository;
        this.questionRepository = questionRepository;
        this.categoryRepository = categoryRepository;
        this.categoryService = categoryService;
    }


    /**
     * This method creates a question for a poll.
     *
     * @return the question which is created
     */
    @Override
    public Question addQuestion(final QuestionCmd questionCmd) {
        Question question = null;
        switch (questionCmd.getQuestionType()) {
            case TEXT_QUESTION:
                question = new Question(questionCmd.getQuestionMessage(), questionCmd.isTextMultiline(),
                    questionCmd.getTextMinimum(), questionCmd.getTextMaximum());
                question.setTextMinBool(questionCmd.isTextMinBool());
                question.setTextMaxBool(questionCmd.isTextMaxBool());
                break;
            case RANGE_QUESTION:
                question = new Question(questionCmd.getQuestionMessage(),
                    questionCmd.getEndValue() == ZERO ? FIVE : questionCmd.getEndValue(),
                    questionCmd.getStartValue(), questionCmd.getStepSize() == ZERO ? ONE : questionCmd.getStepSize(),
                    questionCmd.getBelowMessage() == null ? "" : questionCmd.getBelowMessage(),
                    questionCmd.getAboveMessage() == null ? "" : questionCmd.getAboveMessage());
                break;
            case SLIDER_QUESTION:
                question = new Question(questionCmd.getQuestionMessage(),
                    questionCmd.getEndValue() == ZERO ? ONE : questionCmd.getEndValue(), questionCmd.getStartValue(),
                    questionCmd.getStepSize() == ZERO ? ZERO_DOT_ONE : questionCmd.getStepSize(),
                    questionCmd.getBelowMessage() == null ? "" : questionCmd.getBelowMessage(),
                    questionCmd.getAboveMessage() == null ? "" : questionCmd.getAboveMessage(),
                    questionCmd.isHideValues());
                break;
            case CHOICE_QUESTION:
                question = new Question(questionCmd.getQuestionMessage(), questionCmd.getAnswerPossibilities(),
                    questionCmd.getNumberOfPossibleAnswers(), questionCmd.isUserAnswers());
                break;
            case SORT_QUESTION:
                question = new Question(questionCmd.getQuestionMessage(), questionCmd.getAnswerPossibilities());
                break;
            default:
                return null;
        }
        question.setCategoryId(pollRepository.getOne(Long.valueOf(questionCmd.getPollId())).
            getCategoryList().get(0).getCategoryId());
        pollRepository.findById(Long.valueOf(questionCmd.getPollId())).get().getCategoryList().get(0).getQuestionList().
            add(question);
        questionRepository.save(question);


        return question;
    }

    /**
     * This method removes a selected question.
     *
     * @param categoryId the id of the category where the question is set
     * @param questionId the id of the selected question
     */
    @Override
    public void removeQuestion(final String categoryId, final String questionId) {
        try {
            categoryRepository.findById(Long.valueOf(categoryId))
                .orElseThrow(EntityNotFoundException::new).getQuestionList().remove(questionRepository
                .getOne(Long.valueOf(questionId)));
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
     * This method returns all questions with the categoryId.
     *
     * @param categoryId the id of the category
     * @return All the questions
     */
    @Override
    public List<Question> getAllQuestions(final long categoryId) {
        return questionRepository.findQuestionsByCategoryId(categoryId);
    }

    @Override
    public Question editQuestion(final QuestionCmd questionCmd) {
        final Question question = questionRepository.
            findById(questionCmd.getQuestionId()).orElseThrow(EntityNotFoundException::new);

        switch (questionCmd.getQuestionType()) {
            case TEXT_QUESTION:
                question.setQuestionMessage(questionCmd.getQuestionMessage());
                question.setTextMultiline(questionCmd.isTextMultiline());
                question.setTextMinBool(questionCmd.isTextMinBool());
                question.setTextMaxBool(questionCmd.isTextMaxBool());
                question.setTextMinimum(questionCmd.getTextMinimum());
                question.setTextMaximum(questionCmd.getTextMaximum());
                break;
            case SORT_QUESTION:
                question.setQuestionMessage(questionCmd.getQuestionMessage());
                question.setAnswerPossibilities(questionCmd.getAnswerPossibilities());
                break;
            case RANGE_QUESTION:
                question.setQuestionMessage(questionCmd.getQuestionMessage());
                question.setAboveMessage(questionCmd.getAboveMessage());
                question.setBelowMessage(questionCmd.getBelowMessage());
                question.setStepSize(questionCmd.getStepSize());
                question.setStartValue(questionCmd.getStartValue());
                question.setEndValue(questionCmd.getEndValue());
                break;
            case SLIDER_QUESTION:
                question.setQuestionMessage(questionCmd.getQuestionMessage());
                question.setAboveMessage(questionCmd.getAboveMessage());
                question.setBelowMessage(questionCmd.getBelowMessage());
                question.setStepSize(questionCmd.getStepSize());
                question.setStartValue(questionCmd.getStartValue());
                question.setEndValue(questionCmd.getEndValue());
                question.setHideValues(questionCmd.isHideValues());
                break;
            case CHOICE_QUESTION:
                question.setAnswerPossibilities(questionCmd.getAnswerPossibilities());
                question.setNumberOfPossibleAnswers(questionCmd.getNumberOfPossibleAnswers());
                question.setQuestionMessage(questionCmd.getQuestionMessage());
                question.setQuestionType(questionCmd.getQuestionType());
                question.setUserAnswers(questionCmd.isUserAnswers());
                break;
            default:
                return null;
        }
        questionRepository.save(question);
        return question;

    }

    /**
     * This method changes the category of a question
     * @param questionId the id of the question
     * @param newCategoryId the new category id of the question
     * @param newIndex the new index in the question list of the question
     * @return returns the question
     */
    @Override
    public Question changeCategory(final Long questionId, final Long newCategoryId, final Long newIndex) {
        final Question question = questionRepository.findById(questionId).get();
        final long oldCategoryId = questionRepository.findById(questionId).orElseThrow(EntityNotFoundException::new)
            .getCategoryId();
        final Category oldCategory = categoryRepository.getOne(oldCategoryId);
        final Category newCategory = categoryRepository.getOne(newCategoryId);
        oldCategory.getQuestionList().remove(question);
        question.setCategoryId(newCategoryId);
        newCategory.getQuestionList().add(newIndex.intValue(), question);
        categoryRepository.save(oldCategory);
        categoryRepository.save(newCategory);
        return question;
    }


}
