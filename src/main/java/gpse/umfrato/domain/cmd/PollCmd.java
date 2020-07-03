package gpse.umfrato.domain.cmd;

import gpse.umfrato.domain.poll.Poll;
import lombok.Data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
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

    private int pollStatus;

    private boolean visibility;

    private boolean categoryChange;

    private boolean activated;

    private boolean deactivated;

    public Poll getCmdPoll() {
        LOGGER.info("isActivated: " + activated);
        LOGGER.info("activatedAt: " + activatedAt);
        Calendar activationDate = Calendar.getInstance();
        Calendar deactivationDate = Calendar.getInstance();
        try {
            DateFormat df = new SimpleDateFormat("dd.MM.yyyy&HH:mm");
            Date activation = df.parse(activatedAt);
            activationDate.setTime(activation);
            Date deactivation = df.parse(deactivatedAt);
            deactivationDate.setTime(deactivation);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final Poll poll = new Poll(pollCreator, anonymityStatus, pollName, pollCreatedAt, activationDate,
            deactivationDate, pollStatus, backgroundColor, fontColor, logo, visibility, categoryChange, activated,
            deactivated);
        return poll;
    }


}


