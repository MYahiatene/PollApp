package gpse.umfrato.domain.pollresult;

public interface PollResultService {
    PollResult createPollResult();
    PollResult getPollResult(final Long pollId, final String pollTaker);
}
