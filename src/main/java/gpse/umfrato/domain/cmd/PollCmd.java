package gpse.umfrato.domain.cmd;

import gpse.umfrato.domain.poll.Poll;
import lombok.Data;

import java.text.DateFormat;
import java.text.ParsePosition;
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
        activationDate.set(Integer.parseInt(activatedAt.substring(activatedAt.indexOf('.',3)+1,
            activatedAt.indexOf('&'))) + 1900, Integer.parseInt(activatedAt.substring(activatedAt.indexOf('.') + 1,
            activatedAt.indexOf('.', 3))),
            Integer.parseInt(activatedAt.substring(0, activatedAt.indexOf('.'))),
            Integer.parseInt(activatedAt.substring(activatedAt.indexOf('&')+1, activatedAt.indexOf(':'))),
            Integer.parseInt(activatedAt.substring(activatedAt.indexOf(':') + 1)));
        Calendar deactivationDate = Calendar.getInstance();
        deactivationDate.set(Integer.parseInt(deactivatedAt.substring(deactivatedAt.indexOf('.',3)+1,
            deactivatedAt.indexOf('&'))) + 1900, Integer.parseInt(deactivatedAt.substring(deactivatedAt.indexOf('.') + 1,
            deactivatedAt.indexOf('.', 3))),
            Integer.parseInt(deactivatedAt.substring(0, deactivatedAt.indexOf('.'))),
            Integer.parseInt(deactivatedAt.substring(deactivatedAt.indexOf('&')+1, deactivatedAt.indexOf(':'))),
            Integer.parseInt(deactivatedAt.substring(deactivatedAt.indexOf(':') + 1)));
        LOGGER.info("activationDate: " + activationDate);
        final Poll poll = new Poll(pollCreator, anonymityStatus, pollName, pollCreatedAt, activationDate,
            deactivationDate, pollStatus, backgroundColor, fontColor, logo, visibility, categoryChange, activated,
            deactivated);
        return poll;
    }


}


