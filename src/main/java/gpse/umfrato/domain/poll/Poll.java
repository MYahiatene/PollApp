package gpse.umfrato.domain.poll;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.question.Question;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    private Calendar activatedDate;

    /**
     * This attribute represents the date when the poll was deactivated.
     */
    private Calendar deactivatedDate;

    /**
     * This attribute represents the status of the anonymity if it is anonymous, part-anonymous
     * or not anonymous.
     */
    private String anonymityStatus; // Anonym = 1, andere KA

    /**
     * This attribute represents the chosen background color as hexa.
     */
    private String backgroundColor;

    /**
     * This attribute represents the chosen font color as hexa.
     */
    private String fontColor;

    /**
     * This attribute represents the uploades logo.
     */
    @Lob
    private String logo;


    /**
     * This attribute represents the name of the poll.
     */
    private String pollName;

    /**
     * This attribute represents the status of the poll if it is activated or deactivated.
     */
    private int pollStatus;

    /**
     * This attribute represents the ability to see the number of questions for the participant.
     */
    private boolean visibility;

    /**
     * This attribute represents the ability of the participant to change between the categories/pages of the poll.
     */
    private boolean categoryChange;

    private boolean activated;

    private boolean deactivated;

    /**
     * This attribute represents a question list with all questions of this poll.
     */
    @OneToMany(cascade = CascadeType.ALL)
    private List<Question> questionList = new ArrayList<>();

    /**
     * This attribute represents the link to reach the poll if it is activated.
     */
    private String participationLink;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Category> categoryList = new ArrayList<>();


    /**
     * This constructor receives a poll name and saves in the poll object.
     *
     * @param pollCreator     the name of the user who creates the poll
     * @param pollName        the name of the poll
     * @param anonymityStatus the anonymitystatus of the poll
     * @param createdAt       the date when the poll is created
     * @param activatedAt     the date when the poll activated
     * @param deactivatedAt   the date when the poll deactivates
     * @param pollStatus      the status (activated/deactivated) of the poll
     * @param backgroundColor the background color displayed on the website
     * @param fontColor       the color used for all questions
     * @param logo            the logo displayed on the website
     * @param visibility      whether or not a rolling question number should be displayed
     * @param categoryChange  whether or not it is possible to go to an already answered category
     * @param activated
     * @param deactivated
     */
    public Poll(final String pollCreator, final String anonymityStatus, final String pollName, final String createdAt,
                final Calendar activatedAt, final Calendar deactivatedAt, final int pollStatus,
                final String backgroundColor, final String fontColor, final String logo, final boolean visibility,
                final boolean categoryChange, final boolean activated, final boolean deactivated) {
        this.pollName = pollName;
        this.pollCreator = pollCreator;
        this.anonymityStatus = anonymityStatus;
        this.creationDate = createdAt;
        this.activatedDate = activatedAt;
        this.deactivatedDate = deactivatedAt;
        this.pollStatus = pollStatus;
        this.backgroundColor = backgroundColor;
        this.fontColor = fontColor;
        this.logo = logo;
        this.visibility = visibility;
        this.categoryChange = categoryChange;
        this.activated = activated;
        this.deactivated = deactivated;
    }
}
