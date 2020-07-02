package gpse.umfrato.domain.participationlinks;

import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

@Service
public class ParticipationLinkServiceImpl implements ParticipationLinkService {

    /* default */ final ParticipationLinkRepository participationLinkRepository;

    public ParticipationLinkServiceImpl(final ParticipationLinkRepository participationLinkRepository) {
        this.participationLinkRepository = participationLinkRepository;
    }

    @Override
    public ParticipationLink createParticipationLink(final Long pollId, final String anonymityStatus,
                                                     final String username) {

        final UUID uuid = UUID.randomUUID();
        final ParticipationLink participationLink = new ParticipationLink(pollId, anonymityStatus, username,
            uuid.toString());

        participationLinkRepository.save(participationLink);

        return participationLink;
    }

    @Override
    public Long getPollIdFromParticipationLink(final String participationLink) {
        final List<ParticipationLink> participationLinks = participationLinkRepository.findAll();

        for (final ParticipationLink p : participationLinks) {
            if (p.getParticipationLink().equals(participationLink)) {
                return p.getPollId();
            }
        }
        throw new EntityNotFoundException();
    }

    @Override
    public String getUserFromParticipationLink(final String participationLink) {
        final List<ParticipationLink> participationLinks = participationLinkRepository.findAll();

        for (final ParticipationLink p : participationLinks) {
            if (p.getParticipationLink().equals(participationLink)) {
                return p.getUsername();
            }
        }
        throw new EntityNotFoundException();
    }

    @Override
    public List<ParticipationLink> getAllParticipationLinks() {
        return participationLinkRepository.findAll();
    }

    @Override
    public List<ParticipationLink> getAllParticipationLinks(final Long pollId) {
        return participationLinkRepository.findParticipationLinksByPollId(pollId);
    }

}
