package gpse.umfrato.domain.cmd;

import gpse.umfrato.domain.poll.Poll;
import lombok.Data;

import java.sql.Blob;
import java.time.LocalDateTime;

@Data
public class PollCmd {

    private String id; //shouldn't this b generated, so not be listed here?

    private String pollcreator;

    private String pollCreatedAt;

    //private String lastEditAt;

    private String activatedAt;

    private String deactivatedAt;

    private String anonymityStatus;

    private int hexaBackground;

    private int hexaFont;

    private Blob logo;

    private String pollname;

    private int pollStatus;

    public Poll getCmdPoll() {
        final Poll poll = new Poll(pollcreator, anonymityStatus, pollname, pollCreatedAt, activatedAt, deactivatedAt,
            pollStatus);
        return poll;
    }
}
