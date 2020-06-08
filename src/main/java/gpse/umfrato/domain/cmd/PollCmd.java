package gpse.umfrato.domain.cmd;

import gpse.umfrato.domain.poll.Poll;
import lombok.Data;


@Data
public class PollCmd {

    private String pollId;

    private String pollcreator;

    private String pollCreatedAt;

    //private String lastEditAt;

    private String activatedAt;

    private String deactivatedAt;

    private String anonymityStatus;

    private String pollname;

    private int pollStatus;

    public Poll getCmdPoll() {
        final Poll poll = new Poll(pollcreator, anonymityStatus, pollname, pollCreatedAt, activatedAt, deactivatedAt,
            pollStatus);
        return poll;
    }


}


