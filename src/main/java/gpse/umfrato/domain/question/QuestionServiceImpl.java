package gpse.umfrato.domain.question;

import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.category.CategoryRepository;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.cmd.QuestionCmd;
import gpse.umfrato.domain.poll.PollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@Service
public class QuestionServiceImpl implements QuestionService {

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

    final CategoryService categoryService;

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
     *
     * @param questionCmd the Cmd which includes the necessary details
     * @return returns the question object
     */
    @Override
    public Question addQuestion(final QuestionCmd questionCmd) {
        Question question = null;
        System.out.println(questionCmd.toString());
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
                    questionCmd.getNumberOfPossibleAnswers(), questionCmd.isUserAnswers(), questionCmd.isDropDown());
                break;
            case SORT_QUESTION:
                question = new Question(questionCmd.getQuestionMessage(), questionCmd.getAnswerPossibilities());
                break;
            default:
                return null;
        }
        if(questionCmd.getCategoryType() == null) {
            questionCmd.setCategoryType(questionCmd.getCategoryId());
            questionCmd.setCategoryId(pollRepository.getOne(questionCmd.getPollId()).getCategoryList().get(0).getCategoryId());
        }
        question.setCategoryId(questionCmd.getCategoryType());
        categoryRepository.findById(questionCmd.getCategoryType()).get().getQuestionList().add(question);
        questionRepository.save(question);
        return question;

    }

    /**
     * This method adds a question to a category.
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
     *
     * @param questionCmd the Cmd which includes the necessary details
     * @return returns the edited question object
     */
    @Override
    public Question editQuestion(final QuestionCmd questionCmd) {
        final Question question = questionRepository.
            findById(questionCmd.getQuestionId()).get();
        switch (questionCmd.getQuestionType()) {
            case TEXT_QUESTION:
                question.setQuestionType(questionCmd.getQuestionType());
                question.setQuestionMessage(questionCmd.getQuestionMessage());
                question.setTextMultiline(questionCmd.isTextMultiline());
                question.setTextMinBool(questionCmd.isTextMinBool());
                question.setTextMaxBool(questionCmd.isTextMaxBool());
                question.setTextMinimum(questionCmd.getTextMinimum());
                question.setTextMaximum(questionCmd.getTextMaximum());
                break;
            case SORT_QUESTION:
                question.setQuestionType(questionCmd.getQuestionType());
                question.setQuestionMessage(questionCmd.getQuestionMessage());
                question.setAnswerPossibilities(questionCmd.getAnswerPossibilities());
                break;
            case RANGE_QUESTION:
                question.setQuestionType(questionCmd.getQuestionType());
                question.setQuestionMessage(questionCmd.getQuestionMessage());
                question.setAboveMessage(questionCmd.getAboveMessage());
                question.setBelowMessage(questionCmd.getBelowMessage());
                question.setStepSize(questionCmd.getStepSize());
                question.setStartValue(questionCmd.getStartValue());
                question.setEndValue(questionCmd.getEndValue());
                break;
            case SLIDER_QUESTION:
                question.setQuestionType(questionCmd.getQuestionType());
                question.setQuestionMessage(questionCmd.getQuestionMessage());
                question.setAboveMessage(questionCmd.getAboveMessage());
                question.setBelowMessage(questionCmd.getBelowMessage());
                question.setStepSize(questionCmd.getStepSize());
                question.setStartValue(questionCmd.getStartValue());
                question.setEndValue(questionCmd.getEndValue());
                question.setHideValues(questionCmd.isHideValues());
                break;
            case CHOICE_QUESTION:
                question.setQuestionType(questionCmd.getQuestionType());
                question.setAnswerPossibilities(questionCmd.getAnswerPossibilities());
                question.setNumberOfPossibleAnswers(questionCmd.getNumberOfPossibleAnswers());
                question.setQuestionMessage(questionCmd.getQuestionMessage());
                question.setQuestionType(questionCmd.getQuestionType());
                question.setUserAnswers(questionCmd.isUserAnswers());
                question.setDropDown(questionCmd.isDropDown());
                break;
            default:
                return null;
        }
        final long oldCategoryId = questionRepository.findById(question.getQuestionId()).orElseThrow(EntityNotFoundException::new)
            .getCategoryId();
        final long newCategoryId = questionCmd.getCategoryType();
        if(oldCategoryId != newCategoryId) {
            final Category oldCategory = categoryRepository.getOne(oldCategoryId);
            System.out.println(newCategoryId);
            final Category newCategory = categoryRepository.getOne(newCategoryId);
            oldCategory.getQuestionList().remove(question);
            categoryRepository.save(oldCategory);
            question.setCategoryId(newCategoryId);
            newCategory.getQuestionList().add(question);
            categoryRepository.save(newCategory);
        } else {
            questionRepository.save(question);
        }
        return question;
    }

    /**
     * This method changes the category of a question.
     *
     * @param questionId    the id of the question
     * @param newCategoryId the new category id of the question
     * @param newIndex      the new index in the question list of the question
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

    /**
     * This method changes the category of a question.
     *
     * @param questionId    the id of the question
     * @param newCategoryId the new category id of the question
     * @param newIndex      the new index in the question list of the question
     * @return returns the question
     */
    @Override
    public Question changeQuestionIndex(final Long questionId, final Long newCategoryId, final Long newIndex) {
        final Question question = questionRepository.findById(questionId).get();
        final Category category = categoryRepository.getOne(newCategoryId);
        category.getQuestionList().remove(question);
        category.getQuestionList().add(newIndex.intValue(), question);
        categoryRepository.save(category);
        return question;
    }

    /**
     * Adds a new answerPossibility to the List of answerPossibilities of the given question.
     *
     * @param question
     * @param answer
     */
    @Override
    public void setNewAnswer(final Question question, final String answer) {
        final List<String> list = question.getAnswerPossibilities();
        list.add(answer);
        question.setAnswerPossibilities(list);
        questionRepository.save(question);
    }

    /**
     * Copies a choiceQuestion to a category. Needs to adds every answer separately to the new question.
     *
     * @param question
     * @param pollId
     * @return
     */
    @Override
    public Question addChoiceQuestion(final Question question, final Long pollId) {
        Question newQuestion = null;
        newQuestion = new Question(question.getQuestionMessage(), new ArrayList<>(),
            question.getNumberOfPossibleAnswers(), question.isUserAnswers(), question.isDropDown());
        return copyAnswerPossibilities(question, pollId, newQuestion);
    }

    /**
     * Copies a sortQuestion to a category. Needs to adds every answer separately to the new question.
     *
     * @param question
     * @param pollId
     * @return
     */
    @Override
    public Question addSortQuestion(final Question question, final Long pollId) {
        Question newQuestion = null;
        newQuestion = new Question(question.getQuestionMessage(), new ArrayList<>());
        return copyAnswerPossibilities(question, pollId, newQuestion);
    }

    private Question copyAnswerPossibilities(final Question question, final Long pollId, final Question newQuestion) {
        for (final String answer: question.getAnswerPossibilities()) {
            newQuestion.getAnswerPossibilities().add(answer);
        }
        newQuestion.setCategoryId(pollRepository.findById(pollId)
                .orElseThrow(EntityNotFoundException ::new).getCategoryList().get(0).getCategoryId());
        pollRepository.findById(pollId).orElseThrow(EntityNotFoundException::new)
                .getCategoryList().get(0).getQuestionList().add(newQuestion);
        questionRepository.save(newQuestion);

        return newQuestion;
    }

    /**
     * Copies all questions from one category.
     *
     * @param categoryId
     * @param pollId
     * @param questions
     */
    @Override
    public void copyQuestions(final Long categoryId, final Long pollId, final List<Question> questions) {
        final ListIterator<Question> iterator = questions.listIterator();
        Long indexCounter = 0L;
        // creates a cmd from the question to copy for the addQuestion method
        while (iterator.hasNext()) {
            Question newQuestion = null;
            final Question question = iterator.next();
            final QuestionCmd cmd = new QuestionCmd();
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
            cmd.setCategoryId(question.getCategoryId());
            cmd.setQuestionMessage(question.getQuestionMessage());
            cmd.setAnswerPossibilities(question.getAnswerPossibilities());
            cmd.setQuestionType(question.getQuestionType());
            cmd.setUserAnswers(question.isUserAnswers());
            cmd.setNumberOfPossibleAnswers(question.getNumberOfPossibleAnswers());
            cmd.setCategoryType(question.getCategoryId());
            if (question.getQuestionType().equals(CHOICE_QUESTION)) {
                newQuestion = addChoiceQuestion(question, pollId);
            } else if (question.getQuestionType().equals(SORT_QUESTION)) {
                newQuestion = addSortQuestion(question, pollId);
            } else {
                newQuestion = addQuestion(cmd);
            }
            changeCategory(newQuestion.getQuestionId(), categoryId, indexCounter);
            indexCounter += 1;
        }

    }


}
