package gpse.umfrato.domain.pollresult;

import gpse.umfrato.domain.answer.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;

@Service
public class PollResultServiceImpl implements PollResultService {

    private static final Logger LOGGER = Logger.getLogger("PollResultServiceImpl");
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

    /**
     * This method creates a poll result.
     * @param pollID the id of the poll
     * @param username the username who creates the poll result
     * @return returns the poll result
     */
    @Override
    public PollResult createPollResult(final Long pollID, final String username) {
        return new PollResult(pollID, username);
    }
    /**
     * This method returns all answers of one user.
     * @param input the input
     * @param pollTaker the poll taker
     * @return returns all answers as a list
     */
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

    /**
     * This method returns all the poll results.
     * @param pollId the poll id
     * @return returns all poll results as a list belonging to one poll id
     */
    @Override
    public List<PollResult> getPollResults(final Long pollId) {
        LOGGER.info(pollId.toString());
        return pollResultRepository.findPollResultsByPollId(pollId);
    }

    /**
     * This method returns the poll result belonging to one poll id and one poll taker.
     * @param pollId the id of the poll
     * @param pollTaker the poll taker
     * @return returns the poll result
     */
    @Override
    public PollResult getPollResult(final Long pollId, final String pollTaker) {
        return pollResultRepository.findPollResultByPollIdAndPollTaker(pollId, pollTaker);
    }

}
