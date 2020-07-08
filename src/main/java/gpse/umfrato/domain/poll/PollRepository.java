package gpse.umfrato.domain.poll;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;


/**
 * The repository in which polls are stored.
 */
public interface PollRepository extends JpaRepository<Poll, Long> {
    List<Poll> findTop5ByOrderByLastEditAt();
    List<Poll> findAllByNextSeriesEquals(ZonedDateTime today);
}
