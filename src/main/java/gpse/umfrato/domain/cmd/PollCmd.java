package gpse.umfrato.domain.cmd;

import gpse.umfrato.domain.poll.Poll;
import lombok.Data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * The command design class for polls.
 */
@Data
public class PollCmd {

    /* default */ static final Logger LOGGER = Logger.getLogger("PollCmd");

    private String pollId;

    private String pollCreator;

    private String pollCreatedAt;

    //private String lastEditAt;

    private String activatedAt;

    private String deactivatedAt;

    private String anonymityStatus;

    private String backgroundColor;

    private String fontColor;

    private String logo;

    private String pollName;

    private Integer repeat;

    private String repeatUntil;

    private List<Integer> day;

    private List<Integer> week;

    private List<Integer> month;

    private int pollStatus;

    private boolean visibility;

    private boolean categoryChange;

    private boolean activated;

    private boolean deactivated;

    private Integer stoppingReason;

    private Integer level;


    public Poll getCmdPoll() {
        // parses the activationDAte and deactivationDate from a String to a Calendar
        final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy&HH:mm", Locale.GERMAN);
        final ZonedDateTime activation = ZonedDateTime.parse(activatedAt,df);
        final ZonedDateTime deactivation = ZonedDateTime.parse(deactivatedAt,df);
        final Poll poll = new Poll(pollCreator, anonymityStatus, pollName, ZonedDateTime.parse(activatedAt,df), activation,
            deactivation, pollStatus, backgroundColor, fontColor, logo, visibility, categoryChange, activated,
            deactivated, repeat, repeatUntil, day, week, month, stoppingReason, level, 1L);
        return poll;
    }


}


