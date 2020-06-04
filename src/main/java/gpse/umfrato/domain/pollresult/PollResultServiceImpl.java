package gpse.umfrato.domain.pollresult;

import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.poll.Poll;

import java.util.List;
import java.util.ListIterator;

public class PollResultServiceImpl {

    public PollResult createPollResult(Long pollID, String username){
        return new PollResult(pollID, username);
    }

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

}
