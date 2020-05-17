package gpse.umfrato.domain.pollresult;


import org.springframework.data.jpa.repository.JpaRepository;

public interface PollResultRepository extends JpaRepository<PollResult, Long> {
    PollResult findPollResultByPollId(final Long pollId);
}
