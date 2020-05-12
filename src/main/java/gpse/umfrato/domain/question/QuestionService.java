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
     * @return the question which is created
     */
    Question addQuestion(final String questionMessage, final long pollId,
                         List<String> answerPossibilities,
                         String questionType);

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

    List<Question> getQuestionsFromGroup(final long groupId);
}
