package gpse.umfrato.domain.cmd;

import gpse.umfrato.domain.poll.Poll;
import lombok.Data;

@Data
public class PollCmd {

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

    public Poll getCmdPoll() {
        final Poll poll = new Poll(pollCreator, anonymityStatus, pollName, pollCreatedAt, activatedAt, deactivatedAt,
            pollStatus, backgroundColor, fontColor, logo, visibility, categoryChange);
        return poll;
    }


}


