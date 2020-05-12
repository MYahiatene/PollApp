package gpse.umfrato.domain.poll;


import java.util.List;

public interface PollService {

    /**
     * This method creates a poll with all required parameters.
     *
     * @param pollname        name of the poll
     * @param pollcreator     name of the creator of the poll
     * @param anonymityStatus status of the poll if it is anonymous, part anonymous and non anonymous
     * @param pollStatus      status of the poll if it is activated or deactivated
     * @return the created poll
     */
    Poll createPoll(final String pollcreator, final String anonymityStatus, final String pollname,
                    final int pollStatus);

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
