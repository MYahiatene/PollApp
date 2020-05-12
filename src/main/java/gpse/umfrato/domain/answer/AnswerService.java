package gpse.umfrato.domain.answer;

import java.util.List;

public interface AnswerService {
    /**
     * This method adds an answer to the answer list of the question.
     *
     * @param message    the answer from the user
     * @param questionId the id of the given question
     * @param answerType type of given answer
     * @param username   name of the user creating the answer
     * @return the given answer
     */
    Answer giveAnswer(final String message, final String questionId, final String answerType, final String
        username);

    /**
     * This method deletes an selected answer.
     *
     * @param questionId the id of the question containing the answer
     * @param answerId   the id of the selected answer
     */
    void deleteAnswer(final String questionId, final String answerId);

    /**
     * This method returns the requested answer.
     *
     * @param answerId the id of the requested answer
     * @return the requested answer
     */
    Answer getAnswer(Long answerId);

    /**
     * This method returns all answers from a selected question.
     *
     * @param questionId the id of the selected question
     * @return all answers from a question in a list
     */
    List<Answer> getAllAnswers(String questionId);
}
