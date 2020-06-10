package gpse.umfrato.domain.pollresult;

import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.answer.AnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ListIterator;

@Service
public class PollResultServiceImpl implements PollResultService{

    private final PollResultRepository pollResultRepository;

    /**
     * This class constructor initializes the answer- and question repository.
     *
     * @param pollResultRepository the repository where the pollResults are saved
     * @param answerRepository   the answer repository with answers
     */
    @Autowired
    public PollResultServiceImpl(final PollResultRepository pollResultRepository) {
        this.pollResultRepository = pollResultRepository;
    }

    @Override
    public PollResult createPollResult(Long pollID, String username){
        return new PollResult(pollID, username);
    }

    @Override
    public List<Answer> getUserAnswers(List<PollResult> input, String pollTaker){
        ListIterator<PollResult> iter = input.listIterator();
        while(iter.hasNext())
        {
            PollResult index = iter.next();
            if(index.getPollTaker() == pollTaker){
                return index.getAnswerList();
            }
        }
        return null;
    }

    @Override
    public List<PollResult> getAllPollResults(){ /**TODO! wahrscheinlich nicht mehr nötig? - Jan*/ return null; }

    @Override
    public List<PollResult> getPollResults(final Long pollId)
    {
        return pollResultRepository.findPollResultsByPollId(pollId);
    }

    @Override
    public PollResult getPollResult(final Long pollId, final String pollTaker)
    {
        return pollResultRepository.findPollResultByPollIdAndPollTaker(pollId,pollTaker);
    }

}
