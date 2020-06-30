package gpse.umfrato.domain.participationlinks;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * The repository in which participation links are stored.
 */
public interface ParticipationLinkRepository extends JpaRepository<ParticipationLink, Long> {
    List<ParticipationLink> findParticipationLinksByPollId(Long pollId);
}
