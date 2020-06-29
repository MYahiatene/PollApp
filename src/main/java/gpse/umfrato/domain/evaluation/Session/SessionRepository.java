package gpse.umfrato.domain.evaluation.Session;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findSessionsByPollId(Long pollId);
}
