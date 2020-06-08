package gpse.umfrato.domain.pollresult;

import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.poll.Poll;

import java.util.List;

public interface PollResultService {
    PollResult createPollResult(Long pollID, String username);
    List<Answer> getUserAnswers(List<PollResult> input, String pollTaker);
    List<PollResult> getAllPollResults();
}
