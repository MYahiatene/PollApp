package gpse.umfrato.domain.poll;

import java.util.List;

public interface PollService {

    /**
     * This method creates a poll with all required parameters.
     *
     * @param pollName        name of the poll
     * @param pollCreator     name of the creator of the poll
     * @param anonymityStatus status of the poll if it is anonymous, part anonymous and non anonymous
     * @param createdAt       the date when the poll is created
     * @param pollStatus      status of the poll if it is activated or deactivated
     * @param activatedAt     the date when the poll is activated
     * @param deactivatedAt   the date when the poll deactivates
     * @return the created poll
     */
    Poll createPoll(final String pollCreator, final String anonymityStatus, final String pollName, final String createdAt,
                    final String activatedAt, final String deactivatedAt, final int pollStatus);

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
