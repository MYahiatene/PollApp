package gpse.umfrato.domain.poll;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gpse.umfrato.domain.category.Category;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * This Object represents a poll.
 */
@Entity
@Data
@NoArgsConstructor
public class Poll {

    /**
     * This attribute is an unique id from the object poll.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pollId;

    /**
     * This attribute represents the creator of the poll.
     */
    private String pollCreator;

    /**
     * This attribute represents the date when the poll was created.
     */
    @JsonIgnore
    private Instant creationDate;

    /**
     * This attribute represents the date when the poll was edited the last time.
     */
    @JsonIgnore
    private Instant lastEditAt;

    /**
     * This attribute represents the date when the poll was activated.
     */
    // use Instant instead , cause of time zone
    @JsonIgnore
    private Instant activatedDate;

    /**
     * This attribute represents the date when the poll was deactivated.
     */
    @JsonIgnore
    private Instant deactivatedDate;

    /**
     * This attribute represents the status of the anonymity if it is anonymous, part-anonymous
     * or not anonymous.
     */
    @JsonIgnore
    private String anonymityStatus;

    /**
     * This attribute represents the name of the poll.
     */
    private String pollName;

    /**
     * This attribute represents the status of the poll if it is activated or deactivated.
     */
    private int pollStatus;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Category> categoryList = new ArrayList<>();


    /**
     * This constructor receives a poll name and saves in the poll object.
     * @param pollCreator the name of the user who creates the poll
     * @param pollName the name of the poll
     * @param anonymityStatus the anonymitystatus of the poll
     * @param pollStatus the status (activated/deactivated) of the poll
     */
    public Poll(final String pollCreator, final String pollName, final String anonymityStatus, final int pollStatus) {
        this.pollName = pollName;
        this.pollCreator = pollCreator;
        this.anonymityStatus = anonymityStatus;
        this.pollStatus = pollStatus;
    }

}
