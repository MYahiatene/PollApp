package gpse.umfrato.domain.pollresult;

import gpse.umfrato.domain.answer.Answer;

import java.util.List;

public interface PollResultService {
    PollResult createPollResult(Long pollID, String username);
    List<Answer> getUserAnswers(List<PollResult> input, String pollTaker);
    List<PollResult> getAllPollResults();
    List<PollResult> getPollResults(final Long pollId);
    PollResult getPollResult(final Long pollId, final String pollTaker);
}
