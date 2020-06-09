package gpse.umfrato.domain.pollresult;


import org.springframework.data.jpa.repository.JpaRepository;

public interface PollResultRepository extends JpaRepository<PollResult, Long> {
    /**
     * The method returns an PollResult object from the repository identified by pollId.
     * @param pollId the identifier pollId
     * @return the pollResult object
     */
    PollResult findPollResultByPollId(final Long pollId);

    PollResult findPollResultByPollIdAndPollTaker(final Long pollId, final String pollTaker);
}
