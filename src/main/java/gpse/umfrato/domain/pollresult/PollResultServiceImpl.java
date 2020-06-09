package gpse.umfrato.domain.pollresult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PollResultServiceImpl implements PollResultService{
    private final PollResultRepository pollResultRepository;

    /**
     * This class constructor initializes the answer- and question repository.
     *
     * @param pollResultRepository the repository where the pollResults are saved
     */
    @Autowired
    public PollResultServiceImpl(final PollResultRepository pollResultRepository) {
        this.pollResultRepository = pollResultRepository;
    }

    @Override
    public PollResult createPollResult() {
        return null;
    }

    @Override
    public PollResult getPollResult(final Long pollId, final String pollTaker)
    {
        return pollResultRepository.findPollResultByPollIdAndPollTaker(pollId,pollTaker);
    }

}
