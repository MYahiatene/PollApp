package gpse.umfrato.domain.poll;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gpse.umfrato.domain.category.Category;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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
    @GeneratedValue
    private Long id;

    /**
     * This attribute represents the creator of the poll.
     */
    private String pollCreator;

    /**
     * This attribute represents the date when the poll was created.
     */
    @JsonIgnore
    private String creationDate;

    /**
     * This attribute represents the date when the poll was edited the last time.
     */
    @JsonIgnore
    private String lastEditAt;

    /**
     * This attribute represents the date when the poll was activated.
     */
    // use Instant instead , cause of time zone
    private String activatedDate;

    /**
     * This attribute represents the date when the poll was deactivated.
     */
    @JsonIgnore
    private String deactivatedDate;

    /**
     * This attribute represents the status of the anonymity if it is anonymous, part-anonymous
     * or not anonymous.
     */
    @JsonIgnore
    private String anonymityStatus;

    /**
     * This attribute represents the name of the poll.
     */
    private String pollname;

    /**
     * This attribute represents the status of the poll if it is activated or deactivated.
     */
    private int pollStatus;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    private List<Category> categoryList = new ArrayList<>();


    /**
     * This constructor receives a poll name and saves in the poll object.
     *
     * @param pollCreator     the name of the user who creates the poll
     * @param pollName        the name of the poll
     * @param anonymityStatus the anonymitystatus of the poll
     * @param pollStatus      the status (activated/deactivated) of the poll
     * @param activatedAt     the date when the poll activated
     * @param deactivatedAt   the date when the poll deactivates
     */
    public Poll(final String pollCreator, final String pollName, final String anonymityStatus, final String activatedAt,
                final String deactivatedAt, final int pollStatus) {
        this.pollname = pollName;
        this.pollCreator = pollCreator;
        this.anonymityStatus = anonymityStatus;
        this.activatedDate = activatedAt;
        this.deactivatedDate = deactivatedAt;
        this.pollStatus = pollStatus;
        //this.groupList.add(new Group("Keine Gruppe"));
    }

}
