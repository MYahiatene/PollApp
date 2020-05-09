package gpse.umfrato.domain.poll;


import gpse.umfrato.domain.question.Question;

import java.time.LocalDateTime;
import java.util.List;

public interface PollService {
    /**
     * This method creates a poll with all required parameters.
     *
     * @param pollname        name of the poll
     * @param pollcreator     name of the creator of the poll
     * @param pollCreatedAt   date when the poll was created
     * @param lastEditAt      date when the poll was last edit
     * @param activatedAt     date when the poll was activated
     * @param deactivatedAt   date when the poll was deactivated
     * @param anonymityStatus status of the poll if it is anonymous, part anonymous and non anonymous
     * @param pollStatus      status of the poll if it is activated or deactivated
     * @return the created poll
     */
    Poll createPoll(final String pollname, final String pollcreator, final LocalDateTime pollCreatedAt,
                    final LocalDateTime lastEditAt, final LocalDateTime activatedAt,
                    final LocalDateTime deactivatedAt, final String anonymityStatus, final int pollStatus,
                    final List<Question> questions);

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
