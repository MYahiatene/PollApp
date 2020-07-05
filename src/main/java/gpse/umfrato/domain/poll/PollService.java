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

    /**
     * Saves the new poll in the repository.
     * @param poll
     * @return new poll
     */
    Poll createCopyPoll(final Poll poll);

    Poll createSeriesPoll(final Poll poll);

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

    /**
     * This method deactivates the poll.
     * @param pollId the id of the poll which will be deactivated
     * @return returns the poll deactivation status
     */
    Integer deactivatePoll(final Long pollId);

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

    /**
     * Reformat the date from Calendar to a better readable String.
     * @param date Calendar with date and time information
     * @return date as a String
     */
    String parseDate(final Calendar date);

    /**
     * Checks if a poll should be automatically activated/deactivated and if the activationDAte/deactivationDate is
     * reached, the pollStatus is updated.
     * @param poll
     * @return updated Poll
     */
    Poll checkActivationAndDeactivation(final Poll poll);

    String createSeriesPollName(final Poll poll);

    void calculateNextDate(final Poll poll);

}
