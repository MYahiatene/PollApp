package gpse.umfrato.domain.cmd;

import gpse.umfrato.domain.poll.Poll;
import lombok.Data;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

@Data
public class PollCmd {

    private String pollId;

    private String pollcreator;

    private String pollCreatedAt;

    //private String lastEditAt;

    private String activatedAt;

    private String deactivatedAt;

    private String anonymityStatus;

    private String backgroundColor;

    private String fontColor;

    private String logo;

    private String pollname;

    private int pollStatus;

    private boolean visibility;

    private boolean categoryChange;

    private boolean isActivated;

    private boolean isDeactivated;

    public Poll getCmdPoll() {
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
        final Poll poll = new Poll(pollcreator, anonymityStatus, pollname, pollCreatedAt, activationDate,
            deactivationDate, pollStatus, backgroundColor, fontColor, logo, visibility, categoryChange, isActivated,
            isDeactivated);
        return poll;
    }


}


