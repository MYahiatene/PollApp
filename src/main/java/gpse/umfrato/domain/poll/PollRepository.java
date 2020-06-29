package gpse.umfrato.domain.poll;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PollRepository extends JpaRepository<Poll, Long> {
    List<Poll> findTop5ByOrderByLastEditAt();
}
