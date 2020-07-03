package gpse.umfrato.domain.poll;

import java.util.Calendar;
import java.util.List;

public interface PollService {
    /**
     * This method creates a poll with all required parameters.
     *
     * @param poll the poll with all data to create it
     * @return the created poll
     */
    Poll createPoll(Poll poll);

    Poll createCopyPoll(final Poll poll);

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
    Poll getPoll(Long id);

    /**
     * This method creates a unique username for anonymous polls.
     * @return a number as an anonymous Username
     */
    String createAnonymousUsername();

    /**
     * This method activates the poll.
     * @param pollId the id of the poll which will be activated
     * @return returns the poll activation status
     */
    Integer activatePoll(final Long pollId);

    List<Poll> getLastEditedPolls();

    /**
     * This method edits a poll name.
     * @param pollId the id of the poll which name will be edited
     * @param pollName the new name of the poll
     */
    void editPollName(final Long pollId, final String pollName);

    /**
     * This method deletes a poll.
     * @param pollId the id of the poll which will be deleted
     * @return returns a confirmation String
     */
    String deletePoll(final String pollId);

    String parseDate(final Calendar date);

    Poll checkActivationAndDeactivation(final Poll poll);

}
