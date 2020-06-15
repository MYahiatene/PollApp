package gpse.umfrato.domain.answer;

import java.util.List;

public interface AnswerService {
    /**
     * This method adds an answer to the answer list of the question.
     *
     * @param pollId the id of the poll
     * @param questionId the id of the given question
     * @param username   the username of the user who gives the answer // only for non-anonym
     * @param answerList the list of all possible answers
     * @return the given answer
     */
    Answer giveAnswer(final String username, final Long pollId, final String questionId,
                      final List<String> answerList);

    /**
     * This method deletes an selected answer.
     *
     * @param answerId the id of the selected answer
     */
    String deleteAnswer(final String answerId);

    /**
     * This method returns the requested answer.
     *
     * @param questionId the id of the requested answer
     * @return the requested answer
     */
    List<Answer> getAnswerFromOneQuestion(Long questionId);

    /**
     * This method returns all answers from a selected question.
     *
     * @param pollId the id of the poll
     * @param username the id of the user
     * @return all answers from a question in a list
     */
    //todo: cmd with username and pollId
    List<String> getAllAnswersFromPollByUser(final Long pollId, String username);
}
