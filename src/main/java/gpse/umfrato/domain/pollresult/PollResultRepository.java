package gpse.umfrato.domain.pollresult;


import org.springframework.data.jpa.repository.JpaRepository;

public interface PollResultRepository extends JpaRepository<PollResult, Long> {
    public PollResult findPollResultByPollId(final Long pollId);
}
