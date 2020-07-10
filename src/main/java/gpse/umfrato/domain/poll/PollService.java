package gpse.umfrato.domain.poll;

import java.time.ZoneId;
import java.time.ZonedDateTime;
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
     * This method lets the poll-status progress.
     * @param pollId the id of the poll which will be increased
     * @return returns the poll activation status
     */
    Integer increasePollStatus(final Long pollId);

    /**
     * This method tries to decrease the poll-status.
     * if the given poll has the status 1 it will be returned to 0,
     * otherwise the method will do nothing.
     * @param pollId the id of the poll which will be decreased
     * @return returns the poll activation status
     */
    Integer decreasePollStatus(final Long pollId);

    /**
     * This method returns recently edited polls.
     * @return returns the 5 most recently edited polls
     */
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
    String deletePoll(final Long pollId);

    /**
     * Reformat the date from Calendar to a better readable String.
     * @param date Calendar with date and time information
     * @return date as a String
     */
    String parseDate(final ZonedDateTime date);

    /**
     * Checks if a poll should be automatically activated/deactivated and if the activationDAte/deactivationDate is
     * reached, the pollStatus is updated.
     * @param poll
     * @return updated Poll
     */
    Poll checkActivationAndDeactivation(final Poll poll);

    String createSeriesPollName(final Poll poll);

    ZonedDateTime calculateNextDate(final Poll poll);

    void newLastEdit(Long pollId, ZonedDateTime lastEditAt, String lastEditFrom, ZoneId timezone);
}
