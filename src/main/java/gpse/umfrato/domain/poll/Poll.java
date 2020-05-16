package gpse.umfrato.domain.poll;

import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * This attribute represents the creator of the poll.
     */
    private String pollCreator;

    /**
     * This attribute represents the date when the poll was created.
     */
    private String creationDate;

    /**
     * This attribute represents the date when the poll was edited the last time.
     */
    private String lastEditAt;

    /**
     * This attribute represents the date when the poll was activated.
     */
    // use Instant instead , cause of time zone
    private String activatedDate;

    /**
     * This attribute represents the date when the poll was deactivated.
     */
    private String deactivatedDate;

    /**
     * This attribute represents the status of the anonymity if it is anonymous, part-anonymous
     * or not anonymous.
     */
    private String anonymityStatus;

    /**
     * This attribute represents the name of the poll.
     */
    private String pollname;

    /**
     * This attribute represents the status of the poll if it is activated or deactivated.
     */
    private int pollStatus;


    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Category> categoryList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<User> userList = new ArrayList<>();



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
    }

}
