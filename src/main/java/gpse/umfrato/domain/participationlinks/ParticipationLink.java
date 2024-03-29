package gpse.umfrato.domain.participationlinks;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class ParticipationLink {

    /**
     * This attribute is an unique id from the object ParticipationLink.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long participationLinkId;

    private Long pollId;

    private String username;

    private String generatedParticipationLink;

    public ParticipationLink(final long pollId, final String mailAdress, final String participationLink) {
        this.pollId = pollId;
        this.username = mailAdress;
        this.generatedParticipationLink = participationLink;
    }
}
