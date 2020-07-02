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
    Poll getPoll(String id);

    /**
     * This method creates a unique username for anonym polls.
     * @return a number as an anonym Username
     */
    String createAnonymUsername();

    Integer activatePoll(final Long pollId);

    public String parseDate(final Calendar date);

}
