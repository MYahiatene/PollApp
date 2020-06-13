package gpse.umfrato.domain.poll;


import java.util.List;

public interface PollService {

    /**
     * This method creates a poll with all required parameters.
     *
     * @param poll the poll with all data to create it
     * @return the created poll
     */
    Poll createPoll(Poll poll);

    /**
     * This method returns a list with all polls.
     *
     * @return a list with all polls
     */
    List<Poll> getAllPolls();

    /**
     * This method return a requested poll.
     *
     * @param id the id of the requested poll
     * @return the requested poll
     */
    Poll getPoll(String id);


}
