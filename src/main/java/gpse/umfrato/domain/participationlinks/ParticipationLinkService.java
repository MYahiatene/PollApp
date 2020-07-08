package gpse.umfrato.domain.participationlinks;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public interface ParticipationLinkService {


    URL createParticipationLink() throws MalformedURLException;

    void saveParticipationLink(final Long pollId, final String emailAdress, final String generatedLink);

    Long getPollIdFromParticipationLink(final String participationLink);

    String getUserFromParticipationLink(final String participationLink);

    List<ParticipationLink> getAllParticipationLinks();

    List<ParticipationLink> getAllParticipationLinks(final Long pollId);

    void deleteAllLinks(final Long pollId);

}
