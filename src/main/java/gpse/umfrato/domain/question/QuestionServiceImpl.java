package gpse.umfrato.domain.question;

import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.category.CategoryRepository;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.cmd.QuestionCmd;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollRepository;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.web.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final CategoryService categoryService;

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
                               final PollService pollService, final CategoryRepository categoryRepository,
                               final CategoryService categoryService) {
        this.pollRepository = pollRepository;
        this.questionRepository = questionRepository;
        this.pollService = pollService;
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
            case "TextQuestion":
                question = new Question(questionCmd.getQuestionMessage(), questionCmd.isTextMultiline(), questionCmd.getTextMinimum(), questionCmd.getTextMaximum());
                break;
            case "RangeQuestion":
                question = new Question(questionCmd.getQuestionMessage(), (questionCmd.getEndValue() == 0.0f ? 5.0f : questionCmd.getStepSize()), questionCmd.getStartValue(),
                    (questionCmd.getStepSize() == 0.0f ? 1.0f : questionCmd.getStepSize()), (questionCmd.getBelowMessage() == null ? "" : questionCmd.getBelowMessage()),
                    (questionCmd.getAboveMessage() == null ? "" : questionCmd.getAboveMessage()));
                break;
            case "SliderQuestion":
                question = new Question(questionCmd.getQuestionMessage(), (questionCmd.getEndValue() == 0.0f ? 1.0f : questionCmd.getStepSize()), questionCmd.getStartValue(),
                    (questionCmd.getStepSize() == 0.0f ? 0.1f : questionCmd.getStepSize()), (questionCmd.getBelowMessage() == null ? "" : questionCmd.getBelowMessage()),
                    (questionCmd.getAboveMessage() == null ? "" : questionCmd.getAboveMessage()), questionCmd.isHideValues());
                break;
            case "ChoiceQuestion":
                question = new Question(questionCmd.getQuestionMessage(), questionCmd.getAnswerPossibilities(), questionCmd.getNumberOfPossibleAnswers(), questionCmd.isUserAnswers());
                break;
            default:
                return null;
        }
        question.setCategoryId(pollRepository.findById(Long.valueOf(questionCmd.getPollId())).orElseThrow(EntityNotFoundException::new)
            .getCategoryList().get(0).getCategoryId());
        pollRepository.findById(Long.valueOf(questionCmd.getPollId())).orElseThrow(EntityNotFoundException::new)
            .getCategoryList().get(0).getQuestionList().add(question);
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
                .orElseThrow(EntityNotFoundException::new).getQuestionList().remove(questionRepository.getOne(Long.valueOf(questionId)));
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
     * This method returns all questions with the categoryId
     *
     * @param categoryId the id of the category
     * @return All the questions
     */
    @Override
    public List<Question> getAllQuestions(final long categoryId) {
        return questionRepository.findQuestionsByCategoryId(categoryId);
    }

    @Override
    public Question editQuestion(final Long questionID, final List<String> answerPossibilities, int numberOfPossibleAnswers, String questionMessage, String questionType) {

        Question question = questionRepository.findById(questionID).orElseThrow(EntityNotFoundException::new);
        question.setAnswerPossibilities(answerPossibilities);
        question.setNumberOfPossibleAnswers(numberOfPossibleAnswers);
        question.setQuestionMessage(questionMessage);
        question.setQuestionType(questionType);
        questionRepository.save(question);
        return question;

    }

    @Override
    public Question changeCategory(final Long questionId, final Long oldCategoryId, final Long newCategoryId) {
        Question question = questionRepository.findById(questionId).orElseThrow(EntityNotFoundException::new);
        Category oldCategory = categoryRepository.findById(oldCategoryId).orElseThrow(EntityNotFoundException::new);
        oldCategory.getQuestionList()
            .remove(question);
        Category newCategory = categoryRepository.findById(newCategoryId).orElseThrow(EntityNotFoundException::new);
        newCategory.getQuestionList().add(question);
        question.setCategoryId(newCategoryId);
        return question;
    }


}
