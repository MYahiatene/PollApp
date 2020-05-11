package gpse.umfrato.domain.question;

import java.util.List;

public interface QuestionService {
    /**
     * This method creates a question for a poll.
     *
     * @param questionMessage the given question
     * @param pollId          the id of the poll
     * @return the question which is created
     */
    Question addQuestion(String questionMessage, long pollId);

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
     * @return all questions from a poll
     */
    List<Question> getAllQuestions(final long pollId);

    List<Question> getQuestionsFromGroup(final long groupId);
}
