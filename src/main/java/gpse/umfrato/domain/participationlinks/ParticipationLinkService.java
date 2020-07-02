package gpse.umfrato.domain.participationlinks;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public interface ParticipationLinkService {


    URL createParticipationLink() throws MalformedURLException;

    void saveParticipationLink(Long pollId, String emailAdress, String generatedLink);

    Long getPollIdFromParticipationLink(String participationLink);

    String getUserFromParticipationLink(String participationLink);

    List<ParticipationLink> getAllParticipationLinks();

    List<ParticipationLink> getAllParticipationLinks(Long pollId);

}
