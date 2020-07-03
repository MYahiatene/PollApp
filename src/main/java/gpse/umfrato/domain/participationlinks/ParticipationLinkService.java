package gpse.umfrato.domain.participationlinks;

import java.net.MalformedURLException;
import java.util.List;

public interface ParticipationLinkService {
    ParticipationLink createParticipationLink(final Long pollId, final String username) throws MalformedURLException;
    Long getPollIdFromParticipationLink(final String participationLink);
    String getUserFromParticipationLink(final String participationLink);
    List<ParticipationLink> getAllParticipationLinks();
    List<ParticipationLink> getAllParticipationLinks(final Long pollId);
}
