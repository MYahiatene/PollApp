package gpse.umfrato.domain.question;

import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.category.CategoryRepository;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.cmd.QuestionCmd;
import gpse.umfrato.domain.poll.PollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;

@Service
public class QuestionServiceImpl implements QuestionService {

    /* default */ static final Logger LOGGER = Logger.getLogger("QuestionServiceImpl");
    private static final double FIVE = 5.0;
    private static final double ZERO_DOT_ONE = 0.1;
    private static final double ZERO = 0.0;
    private static final double ONE = 1.0;


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
     * This method adds a question.
     * @param questionCmd the Cmd which includes the necessary details
     * @return returns the question object
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
                    questionCmd.getStartValue(), questionCmd.getEndValue(),
                        questionCmd.getStepSize() == ZERO ? ONE : questionCmd.getStepSize(),
                        questionCmd.getBelowMessage() == null ? "" : questionCmd.getBelowMessage(),
                        questionCmd.getAboveMessage() == null ? "" : questionCmd.getAboveMessage());
                break;
            case SLIDER_QUESTION:
                question = new Question(questionCmd.getQuestionMessage(),
                    questionCmd.getStartValue(), questionCmd.getEndValue(),
                    questionCmd.getStepSize() == ZERO ? ONE : questionCmd.getStepSize(),
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
     * This method creates a question for a poll.
     *
     * @return the question which is created
     */
    @Override
    public Question addQuestion(final Long pollId, final Question question) {
        question.setCategoryId(categoryRepository.findCategoriesByPollId(pollId).get(0).getCategoryId());
        final Category category = categoryRepository.findCategoriesByPollId(pollId).get(0);
        category.getQuestionList().add(question);
        categoryRepository.save(category);
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
    public void removeQuestion(final Long categoryId, final Long questionId) {
        try {
            categoryRepository.findById(categoryId)
                .orElseThrow(EntityNotFoundException::new).getQuestionList().remove(questionRepository
                .getOne(questionId));
            questionRepository.deleteById(questionId);

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
    /**
     * This method edits a question.
     * @param questionCmd the Cmd which includes the necessary details
     * @return returns the edited question object
     */
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
     * This method changes the category of a question.
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
        newCategory.getQuestionList().add(question);
        categoryRepository.save(oldCategory);
            categoryRepository.save(newCategory);
        return question;
    }

    @Override
    public void setNewAnswer(final Question question, final String answer) {
        List<String> list = question.getAnswerPossibilities();
        list.add(answer);
        question.setAnswerPossibilities(list);
        questionRepository.save(question);
    }

    @Override
    public Question addChoiceQuestion (final Question question, final Long pollId) {
        LOGGER.info("start addChoiceQuestion");
        LOGGER.info("question: " + question);
        Question newQuestion = null;
        newQuestion = new Question(question.getQuestionMessage(), new ArrayList<>(),
            question.getNumberOfPossibleAnswers(), question.isUserAnswers());
        final ListIterator<String> it = question.getAnswerPossibilities().listIterator();
        while(it.hasNext()) {
            String answer = it.next();
            newQuestion.getAnswerPossibilities().add(answer);
        }
        newQuestion.setCategoryId(pollRepository.findById(pollId)
            .orElseThrow(EntityNotFoundException::new).getCategoryList().get(0).getCategoryId());
        pollRepository.findById(pollId).orElseThrow(EntityNotFoundException::new)
            .getCategoryList().get(0).getQuestionList().add(newQuestion);
        questionRepository.save(newQuestion);

        return newQuestion;
    }

    @Override
    public void copyQuestions(final Long categoryId, final Long pollId, final List<Question> questions) {
        final ListIterator<Question> iterator = questions.listIterator();
        Long indexCounter = 0L;
        while(iterator.hasNext()) {
            Question newQuestion = null;
            Question question = iterator.next();
            QuestionCmd cmd = new QuestionCmd();
            cmd.setPollId(pollId);
            cmd.setEndValue(question.getEndValue());
            cmd.setStartValue(question.getStartValue());
            cmd.setStepSize(question.getStepSize());
            cmd.setBelowMessage(question.getBelowMessage());
            cmd.setAboveMessage(question.getAboveMessage());
            cmd.setHideValues(question.isHideValues());
            cmd.setTextMultiline(question.isTextMultiline());
            cmd.setTextMinimum(question.getTextMinimum());
            cmd.setTextMaximum(question.getTextMaximum());
            cmd.setTextMinBool(question.isTextMinBool());
            cmd.setTextMaxBool(question.isTextMaxBool());
            // question.isHasConsistencyRelationship(),
            cmd.setCategoryId(question.getCategoryId());
            cmd.setQuestionMessage(question.getQuestionMessage());
            cmd.setAnswerPossibilities(question.getAnswerPossibilities());
            cmd.setQuestionType(question.getQuestionType());
            cmd.setUserAnswers(question.isUserAnswers());
            cmd.setNumberOfPossibleAnswers(question.getNumberOfPossibleAnswers());
            if (question.getQuestionType().equals(CHOICE_QUESTION) || question.getQuestionType().equals(SORT_QUESTION)) {
                newQuestion = addChoiceQuestion(question, pollId);
            } else {
                newQuestion = addQuestion(cmd);
            }
            Question finalQuestion = changeCategory(newQuestion.getQuestionId(), categoryId, indexCounter);
            indexCounter += 1;
        }

    }


}
