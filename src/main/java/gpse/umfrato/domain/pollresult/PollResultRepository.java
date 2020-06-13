package gpse.umfrato.domain.pollresult;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PollResultRepository extends JpaRepository<PollResult, Long> {
    /**
     * The method returns an PollResult object from the repository identified by pollId.
     * @param pollId the identifier pollId
     * @return the pollResult object
     */
    List<PollResult> findPollResultsByPollId(final Long pollId);

    PollResult findPollResultByPollIdAndPollTaker(final Long pollId, final String pollTaker);

}
