package gpse.umfrato.domain.participationLinks;

import gpse.umfrato.domain.cmd.PollCmd;
import gpse.umfrato.web.BadRequestException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

@Service
public class ParticipationLinkImpl implements ParticipationLinkService {

    final ParticipationLinkRepository participationLinkRepository;

    public ParticipationLinkImpl(final ParticipationLinkRepository participationLinkRepository) {
        this.participationLinkRepository = participationLinkRepository;
    }

    @Override
    public ParticipationLink createParticipationLink(Long pollId, String username) {

        final UUID uuid = UUID.randomUUID();
        //final String urlUuid = "/" + uuid.toString();
        //final URL invitationLink = new URL("http", "localhost", 8080, urlUuid);

        ParticipationLink p = new ParticipationLink(pollId, username, uuid.toString());

        participationLinkRepository.save(p);

        return p;
    }

    @Override
    public Long getPollIdFromParticipationLink(String participationLink) {
        List<ParticipationLink> participationLinks = participationLinkRepository.findAll();

        for (ParticipationLink p : participationLinks) {
            if (p.getParticipationLink().equals(participationLink)) {
                return p.getPollId();
            }
        }
        throw new EntityNotFoundException();
    }

    @Override
    public String getUserFromParticipationLink(String participationLink) {
        List<ParticipationLink> participationLinks = participationLinkRepository.findAll();

        for (ParticipationLink p : participationLinks) {
            if (p.getParticipationLink().equals(participationLink)) {
                return p.getUsername();
            }
        }
        throw new EntityNotFoundException();
    }

    @Override
    public List<ParticipationLink> getAllParticipationLinks() {
        //final List<ParticipationLink> pLinks = participationLinkRepository.findParticipationLinksByPollId(
          //  Long.valueOf(pollId));

        return participationLinkRepository.findAll();

        /*if (pLinks.isEmpty()) {
            throw new BadRequestException();
        }*/

        //return pLinks;
    }

}
