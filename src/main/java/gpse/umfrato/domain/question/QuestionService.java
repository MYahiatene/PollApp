package gpse.umfrato.domain.question;

import java.util.List;
public interface QuestionService {

    Question addQuestion(final Long pollId, final Question question);

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
