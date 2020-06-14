package gpse.umfrato.domain.question;

import java.util.List;
public interface QuestionService {
    /**
     * This method creates a question for a poll.
     *
     * @param questionMessage     the given question
     * @param pollId              the id of the poll where the question belongs to
     * @param answerPossibilities a list of possible answers to the question
     * @param questionType        the type how the answers should be like
     * @param endValue            the end Value of a range, for rangeQuestions
     * @param startValue          the start Value of a range, for rangeQuestions
     * @param stepSize            the size of the steps between the start and end value of a rangeQuestion
     * @param belowMessage        the message for the meaning of the start value of a range Question
     * @param aboveMessage        the message for the meaning of the end value of a range Question
     * @param hideValues
     * @param questionIndex       the index of a question inside its category
     * @param textMultiline
     * @param textMinimum         the minimal number of letters needed in a textfield
     * @param textMaximum         the maximal number of letters possible in a textfield
     * @return the question which is created
     */
    Question addQuestion(final String pollId,
                                final String questionMessage,
                                final List<String> answerPossibilities,
                                final String questionType,
                                final int endValue,
                                final int startValue,
                                final int stepSize,
                                final String belowMessage,
                                final String aboveMessage,
                                final boolean hideValues,
                                final int questionIndex,
                                final Boolean textMultiline,
                                final int textMinimum,
                                final int textMaximum);

    /**
     * This method removes a selected question.
     *
     * @param pollId     the id of the poll where the question is setted
     * @param questionId the id of the selectes question
     */
    void removeQuestion(String pollId, String questionId);

    /**
     * This method returns the requested question.
     *
     * @param questionId the id of the requested question
     * @return the requested question
     */
    Question getQuestion(Long questionId);

    /**
     * This method returns all questions from a poll.
     *
     * @param pollId the id of the poll
     * @return all questions from a poll
     */
    List<Question> getAllQuestions(final long pollId);

    List<Question> getQuestionsFromCategory(final long categoryId);
}
