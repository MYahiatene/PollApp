package gpse.umfrato.domain.pollresult;

import gpse.umfrato.domain.answer.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ListIterator;
import java.util.function.Supplier;
import java.util.logging.Logger;

@Service
public class PollResultServiceImpl implements PollResultService {

    private static final Logger LOGGER = Logger.getLogger("PollResultServiceImpl");;
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
    public PollResult createPollResult(final Long pollID, final String username) {
        return new PollResult(pollID, username);
    }

    @Override
    public List<Answer> getUserAnswers(final List<PollResult> input, final String pollTaker) {
        final ListIterator<PollResult> iter = input.listIterator();
        while (iter.hasNext()) {
            final PollResult index = iter.next();
            if (index.getPollTaker() == pollTaker) {
                return index.getAnswerList();
            }
        }
        return null;
    }

    @Override
    public List<PollResult> getAllPollResults() {
        /**TODO! wahrscheinlich nicht mehr nötig? - Jan*/ return null;
    }

    @Override
    public List<PollResult> getPollResults(final Long pollId) {
        LOGGER.info(pollId.toString());
        return pollResultRepository.findPollResultsByPollId(pollId);
    }

    @Override
    public PollResult getPollResult(final Long pollId, final String pollTaker) {
        // Das funktioniert!
        LOGGER.info(pollId + pollTaker);
        // LOGGER.info(pollResultRepository.findPollResultByPollIdAndPollTaker(pollId, pollTaker).toString());
        try {
            return pollResultRepository.findPollResultByPollIdAndPollTaker(pollId, pollTaker);
        } catch(NullPointerException e) {
            return null;
        }
    }

}
