package gpse.umfrato.domain.participationlinks;

import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

@Service
public class ParticipationLinkServiceImpl implements ParticipationLinkService {

    /* default */ static final int DEFAULT_PORT = 8080;

    /* default */ final ParticipationLinkRepository participationLinkRepository;

    public ParticipationLinkServiceImpl(final ParticipationLinkRepository participationLinkRepository) {
        this.participationLinkRepository = participationLinkRepository;
    }

    @Override
    public URL createParticipationLink()
        throws MalformedURLException {

        final UUID uuid = UUID.randomUUID();
        final String urlUuid = "/" + uuid.toString();
        final URL invitationLink = new URL("http", "localhost", DEFAULT_PORT, urlUuid);

        return invitationLink;
    }

    @Override
    public void saveParticipationLink(final Long pollId, final String emailAdress, final String generatedLink) {

        final ParticipationLink participationLink = new ParticipationLink(pollId, emailAdress,
            generatedLink);
        participationLinkRepository.save(participationLink);
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
