package gpse.umfrato.domain.poll;

import gpse.umfrato.domain.category.Category;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.ZonedDateTime;
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
    private ZonedDateTime creationDate;

    /**
     * This attribute represents the date when the poll was edited the last time.
     */
    // @JsonIgnore
    private ZonedDateTime lastEditAt;

    /**
     * This attribute represents the user who edited the poll the last time.
     */
    // @JsonIgnore
    private String lastEditFrom;

    /**
     * This attribute represents the date and time when the poll is/was activated.
     */
    private ZonedDateTime activatedDate;

    /**
     * This attribute represents the date and time when the poll is/was deactivated.
     */
    private ZonedDateTime deactivatedDate;

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

    /**
     * This attribute represents if the poll should be activated automatically at the given activationDate.
     */
    private boolean activated;

    /**
     * This attribute represents if the poll should be deactivated automatically at the given activationDate.
     */
    private boolean deactivated;

    /**
     * This attribute represents the link to reach the poll if it is activated.
     */
    private String participationLink;

    @OneToMany(orphanRemoval = true)
    private List<Category> categoryList = new ArrayList<>();

    private Integer repeat;

    private String repeatUntil;

    @ElementCollection
    private List<Integer> day = new ArrayList<>();

    @ElementCollection
    private List<Integer> week = new ArrayList<>();

    @ElementCollection
    private List<Integer> month = new ArrayList<>();

    private Integer stoppingReason;

    private Integer level;

    private Long seriesCounter;

    private ZonedDateTime nextSeries;

    private Long prevInSeries = -1L;

    /**
     * This constructor receives a poll name and saves in the poll object.
     *
     * @param pollCreator     the name of the user who creates the poll
     * @param pollName        the name of the poll
     * @param anonymityStatus the anonymityStatus of the poll
     * @param createdAt       the date when the poll is created
     * @param activatedAt     the date when the poll activated
     * @param deactivatedAt   the date when the poll deactivates
     * @param pollStatus      the status (activated/deactivated) of the poll
     * @param backgroundColor the background color displayed on the website
     * @param fontColor       the color used for all questions
     * @param logo            the logo displayed on the website
     * @param visibility      whether or not a rolling question number should be displayed
     * @param categoryChange  whether or not it is possible to go to an already answered category
     * @param activated       whether the poll should be activated automatically
     * @param deactivated     whether the poll should be deactivated automatically
     */
    public Poll(final String pollCreator, final String anonymityStatus, final String pollName, final ZonedDateTime createdAt,
                final ZonedDateTime activatedAt, final ZonedDateTime deactivatedAt, final int pollStatus,
                final String backgroundColor, final String fontColor, final String logo, final boolean visibility,
                final boolean categoryChange, final boolean activated, final boolean deactivated, final Integer repeat,
                final String repeatUntil, final List<Integer> day, final List<Integer> week, final List<Integer> month,
                final Integer stoppingReason, final Integer level, final Long seriesCounter) {
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
        this.repeat = repeat;
        this.repeatUntil = repeatUntil;
        this.day = day;
        this.week = week;
        this.month = month;
        this.stoppingReason = stoppingReason;
        this.level = level;
        this.seriesCounter = seriesCounter;
    }
}
