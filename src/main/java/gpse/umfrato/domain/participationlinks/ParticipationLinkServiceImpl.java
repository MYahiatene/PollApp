package gpse.umfrato.domain.participationlinks;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

@Service
public class ParticipationLinkServiceImpl implements ParticipationLinkService {

    static final int DEFAULT_PORT = 8080;
    static final String PARTICIPANT = "/participant/";
    static final String HTTP = "http";
    static final String LOCALHOST = "localhost";

    final ParticipationLinkRepository participationLinkRepository;

    public ParticipationLinkServiceImpl(final ParticipationLinkRepository participationLinkRepository) {
        this.participationLinkRepository = participationLinkRepository;
    }

    @Override
    public URL createParticipationLink()
        throws MalformedURLException {

        final UUID uuid = UUID.randomUUID();
        final String urlUuid = PARTICIPANT + uuid.toString();
        final URL invitationLink = new URL(HTTP, LOCALHOST, DEFAULT_PORT, urlUuid);

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
        try {
            final URL link = new URL(HTTP, LOCALHOST, DEFAULT_PORT,
                PARTICIPANT + participationLink);
            return participationLinkRepository.findByGeneratedParticipationLink(link.toExternalForm()).getPollId();
        } catch (EntityNotFoundException | MalformedURLException e) {
            e.printStackTrace();
            return -1L;
        }
    }

    @Override
    public String getUserFromParticipationLink(final String participationLink) {
        try {
            final URL link = new URL(HTTP, LOCALHOST, DEFAULT_PORT,
                PARTICIPANT + participationLink);
            return participationLinkRepository.findByGeneratedParticipationLink(link.toExternalForm()).getUsername();
        } catch (EntityNotFoundException | MalformedURLException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public List<ParticipationLink> getAllParticipationLinks() {
        return participationLinkRepository.findAll();
    }

    @Override
    public List<ParticipationLink> getAllParticipationLinks(final Long pollId) {
        return participationLinkRepository.findParticipationLinksByPollId(pollId);
    }

    @Override
    public void deleteAllLinks(final Long pollId) {
        participationLinkRepository.deleteAllByPollId(pollId);
    }

    @Override
    public void updateLinks(final Long oldPollId, final Long newPollId) {
        final List<ParticipationLink> links = participationLinkRepository.findParticipationLinksByPollId(oldPollId);
        for (final ParticipationLink pl : links) {
            pl.setPollId(newPollId);
        }
        participationLinkRepository.saveAll(links);
    }

}
