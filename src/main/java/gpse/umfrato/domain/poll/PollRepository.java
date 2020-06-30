package gpse.umfrato.domain.poll;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The repository in which polls are stored.
 */
public interface PollRepository extends JpaRepository<Poll, Long> {
}
