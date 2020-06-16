package gpse.umfrato.domain.participationlinks;

import java.net.MalformedURLException;
import java.util.List;

public interface ParticipationLinkService {
    ParticipationLink createParticipationLink(Long pollId, String username) throws MalformedURLException;
    Long getPollIdFromParticipationLink(String participationLink);
    String getUserFromParticipationLink(String participationLink);
    List<ParticipationLink> getAllParticipationLinks();
}
