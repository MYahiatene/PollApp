package gpse.umfrato.domain.pollresult;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
/**
 * The repository in which poll results are stored.
 */
public interface PollResultRepository extends JpaRepository<PollResult, Long> {
    /**
     * The method returns an PollResult object from the repository identified by pollId.
     * @param pollId the identifier pollId
     * @return the pollResult object
     */
    List<PollResult> findPollResultsByPollId(final Long pollId);

    /**
     * This method returns the poll result belonging to a poll id and a poll taker.
     * @param pollId the poll id of the requested poll result
     * @param pollTaker the poll taker who took the poll
     * @return returns the poll result
     */
    PollResult findPollResultByPollIdAndPollTaker(final Long pollId, final String pollTaker);

    void deleteAllByPollId(final Long pollId);

}
