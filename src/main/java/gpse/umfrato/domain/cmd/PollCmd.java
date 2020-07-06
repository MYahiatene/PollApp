package gpse.umfrato.domain.cmd;

import gpse.umfrato.domain.poll.Poll;
import lombok.Data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private int pollStatus;

    private boolean visibility;

    private boolean categoryChange;

    private boolean activated;

    private boolean deactivated;

    public Poll getCmdPoll() {
        // parses the activationDAte and deactivationDate from a String to a Calendar
        final Calendar activationDate = Calendar.getInstance();
        final Calendar deactivationDate = Calendar.getInstance();
        try {
            final DateFormat df = new SimpleDateFormat("dd.MM.yyyy&HH:mm", Locale.GERMAN);
            final Date activation = df.parse(activatedDate);
            activationDate.setTime(activation);
            final Date deactivation = df.parse(deactivatedDate);
            deactivationDate.setTime(deactivation);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final Poll poll = new Poll(pollCreator, anonymityStatus, pollName, creationDate, activationDate,
            deactivationDate, pollStatus, backgroundColor, fontColor, logo, visibility, categoryChange, activated,
            deactivated);
        return poll;
    }


}


