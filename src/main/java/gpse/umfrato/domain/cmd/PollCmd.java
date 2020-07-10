package gpse.umfrato.domain.cmd;

import gpse.umfrato.domain.poll.Poll;
import lombok.Data;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

/**
 * The command design class for polls.
 */
@Data
public class PollCmd {

    static final Logger LOGGER = Logger.getLogger("PollCmd");

    private Long pollId;

    private String pollCreator;

    private String creationDate;

    private String lastEditAt;

    private String lastEditFrom;

    private String activatedDate;

    private String deactivatedDate;

    private String participationLink;

    private List<CategoryCmd> categoryList;

    private String anonymityStatus;

    private String backgroundColor;

    private String fontColor;

    private String logo;

    private String pollName;

    private Integer repeat;

    private String repeatUntil;

    private Integer stoppingReason;

    private List<Integer> day;

    private List<Integer> week;

    private List<Integer> month;

    private int pollStatus;

    private boolean visibility;

    private boolean categoryChange;

    private boolean activated;

    private boolean deactivated;

    private Integer level;

    private Long seriesCounter;

    private ZonedDateTime nextSeries;

    private Long prevInSeries = -1L;

    private Boolean ownDesign;

    private Boolean checkLeapYear = false;

    private String seriesPollName;

    public Poll getCmdPoll(final ZoneOffset userOffset) {
        // parses the activationDAte and deactivationDate from a String to a ZonedDateTime
        // System.out.println(this);
        final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy&HH:mm");
        LocalDateTime localActivation = null;
        final LocalDateTime localCreation = LocalDateTime.parse(creationDate, df);
        final ZoneId timeZone = ZoneId.ofOffset("UTC", userOffset);
        ZonedDateTime activation = null;
        ZonedDateTime deactivation = null;
        if (activatedDate != null) {
            localActivation = LocalDateTime.parse(activatedDate, df);
            activation = localActivation.atZone(timeZone);
        }
        LocalDateTime localDeactivation = null;
        if (deactivatedDate != null) {
            localDeactivation = LocalDateTime.parse(deactivatedDate, df);
            deactivation = localDeactivation.atZone(timeZone);
        }
        // final ZonedDateTime lastEditAtZone = LocalDateTime.parse(lastEditAt, df).atZone(timeZone);


        final ZonedDateTime creation = localCreation.atZone(timeZone);
        final Poll poll = new Poll(pollCreator, anonymityStatus, pollName, creation, activation,
            deactivation, pollStatus, backgroundColor, fontColor, logo, visibility, categoryChange, activated,
            deactivated, ownDesign, repeat, repeatUntil, day, week, month, stoppingReason, level, 1L, checkLeapYear,
            creation, lastEditFrom);
        poll.setLastEditAt(ZonedDateTime.now());
        return poll;
    }
}


