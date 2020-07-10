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
    private String anonymityStatus; // Anonym = 1, Teilanonym = 2, Nicht-Anonym = 3

    private Boolean ownDesign;
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
     * This attribute represents the status of the poll if it is editable, ready activated or deactivated.
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

    /**
     * This attribute represents the number of reputations of the given timeslot.
     */
    private Integer repeat;

    /**
     * This attribute represents the attribute when a series of polls should be stopped. Can be a date or a number.
     */
    private String repeatUntil;

    /**
     * This attribute represents the days of a week, month or year, depending on the context at which the series should
     * have a new poll.
     */
    @ElementCollection
    private List<Integer> day = new ArrayList<>();

    /**
     * This attribute represents the weeks of a month or year, depending on the context at which the series should
     * have a new poll. Only if there is minimum one day given.
     */
    @ElementCollection
    private List<Integer> week = new ArrayList<>();

    /**
     * This attribute represents the months of a year at which the series should have a new poll. Only if there is
     * minimum one day given.
     */
    @ElementCollection
    private List<Integer> month = new ArrayList<>();

    /**
     * This attribute represents the reason to stop a series of polls. Is a number: 0 = Date, 1 = number of polls, 2 =
     * number of participants.
     */
    private Integer stoppingReason;

    /**
     * This attribute represents the level of reputation. Represented by 0 for days, 1 for weeks, 2 for months and 3 for
     * years
     */
    private Integer level;

    /**
     * This attribute represents which index the poll has within a series.
     */
    private Long seriesCounter;

    /**
     * This attribute represents the next poll in the series.
     */
    private ZonedDateTime nextSeries;

    /**
     * This attribute represents the pollId of the previous poll in the series.
     */
    private Long prevInSeries = -1L;

    /**
     * This attribtue represents the default pollName of a series with placeholders.
     */
    private String seriesPollName;

    /**
     * This attribute represents if leap years should be payed attention in the calculations of a series.
     */
    private Boolean checkLeapYear;

    /**
     * This constructor receives a poll name and saves in the poll object.
     *
     * @param pollCreator     the name of the user who creates the poll
     * @param pollName        the name of the poll
     * @param anonymityStatus the anonymityStatus of the poll
     * @param createdAt       the date when the poll is created
     * @param activatedDate   the date when the poll activated
     * @param deactivatedDate the date when the poll deactivates
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
                final ZonedDateTime activatedDate, final ZonedDateTime deactivatedDate, final int pollStatus,
                final String backgroundColor, final String fontColor, final String logo, final boolean visibility,
                final boolean categoryChange, final boolean activated, final boolean deactivated, final boolean ownDesign, final Integer repeat,
                final String repeatUntil, final List<Integer> day, final List<Integer> week, final List<Integer> month,
                final Integer stoppingReason, final Integer level, final Long seriesCounter, final Boolean checkLeapYear,
                final ZonedDateTime lastEditAt, final String lastEditFrom) {
        this.pollName = pollName;
        this.pollCreator = pollCreator;
        this.anonymityStatus = anonymityStatus;
        this.creationDate = createdAt;
        this.activatedDate = activatedDate;
        this.deactivatedDate = deactivatedDate;
        this.pollStatus = pollStatus;
        this.backgroundColor = backgroundColor;
        this.fontColor = fontColor;
        this.logo = logo;
        this.visibility = visibility;
        this.categoryChange = categoryChange;
        this.activated = activated;
        this.deactivated = deactivated;
        this.ownDesign = ownDesign;
        this.repeat = repeat;
        this.repeatUntil = repeatUntil;
        this.day = day;
        this.week = week;
        this.month = month;
        this.stoppingReason = stoppingReason;
        this.level = level;
        this.seriesCounter = seriesCounter;
        this.checkLeapYear = checkLeapYear;
    }
}
