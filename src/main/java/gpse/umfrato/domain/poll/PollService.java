package gpse.umfrato.domain.poll;


import java.util.List;

public interface PollService {
    /**
     * This method creates a poll with all required parameters.
     *
     * @param pollname        name of the poll
     * @return the created poll
     */
    Poll createPoll(final String pollname, final String pollcreator,final String anonymityStatus, final int pollStatus);

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
