package gpse.umfrato.domain.pollresult;

import gpse.umfrato.domain.answer.Answer;

import java.util.List;

public interface PollResultService {
    /**
     * This method creates a poll result.
     * @param pollID the id of the poll
     * @param username the username who creates the poll result
     * @return returns the poll result
     */
    PollResult createPollResult(Long pollID, String username);

    /**
     * This method returns all answers of one user.
     * @param input the input
     * @param pollTaker the poll taker
     * @return returns all answers as a list
     */
    List<Answer> getUserAnswers(List<PollResult> input, String pollTaker);

    /**
     * This method returns all the poll results.
     * @param pollId the poll id
     * @return returns all poll results as a list belonging to one poll id
     */
    List<PollResult> getPollResults(final Long pollId);

    /**
     * This method returns the poll result belonging to one poll id and one poll taker.
     * @param pollId the id of the poll
     * @param pollTaker the poll taker
     * @return returns the poll result
     */
    PollResult getPollResult(final Long pollId, final String pollTaker);
}
