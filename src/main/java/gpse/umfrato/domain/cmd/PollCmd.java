package gpse.umfrato.domain.cmd;

import gpse.umfrato.domain.poll.Poll;
import lombok.Data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    private String repeat;

    private String repeatUntil;

    private String[] day;

    private String[] week;

    private String[] month;

    private int pollStatus;

    private boolean visibility;

    private boolean categoryChange;

    private boolean activated;

    private boolean deactivated;

    private int stoppingReason;

    private int level;


    public Poll getCmdPoll() {
        // parses the activationDAte and deactivationDate from a String to a Calendar
        final Calendar activationDate = Calendar.getInstance();
        final Calendar deactivationDate = Calendar.getInstance();
        try {
            final DateFormat df = new SimpleDateFormat("dd.MM.yyyy&HH:mm", Locale.GERMAN);
            final Date activation = df.parse(activatedAt);
            activationDate.setTime(activation);
            final Date deactivation = df.parse(deactivatedAt);
            deactivationDate.setTime(deactivation);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final Poll poll = new Poll(pollCreator, anonymityStatus, pollName, pollCreatedAt, activationDate,
            deactivationDate, pollStatus, backgroundColor, fontColor, logo, visibility, categoryChange, activated,
            deactivated, repeat, repeatUntil, day, week, month, stoppingReason, level, 1L);
        return poll;
    }


}


