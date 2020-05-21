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
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPollcreator() {
        return pollcreator;
    }

    public void setPollcreator(String pollCreator) {
        this.pollcreator = pollCreator;
    }

    public LocalDateTime getPollCreatedAt() {
        return pollCreatedAt;
    }

    public void setPollCreatedAt(LocalDateTime pollCreatedAt) {
        this.pollCreatedAt = pollCreatedAt;
    }

    public LocalDateTime getLastEditAt() {
        return lastEditAt;
    }

    public void setLastEditAt(LocalDateTime lastEditAt) {
        this.lastEditAt = lastEditAt;
    }

    public LocalDateTime getActivatedAt() {
        return activatedAt;
    }

    public void setActivatedAt(LocalDateTime activatedAt) {
        this.activatedAt = activatedAt;
    }

    public LocalDateTime getDeactivatedAt() {
        return deactivatedAt;
    }

    public void setDeactivatedAt(LocalDateTime deactivatedAt) {
        this.deactivatedAt = deactivatedAt;
    }

    public String getAnonymityStatus() {
        return anonymityStatus;
    }

    public void setAnonymityStatus(String anonymityStatus) {
        this.anonymityStatus = anonymityStatus;
    }

    public String getPollname() {
        return pollname;
    }

    public void setPollname(String pollname) {
        this.pollname = pollname;
    }

    public int getPollStatus() {
        return pollStatus;
    }

    public void setPollStatus(int pollStatus) {
        this.pollStatus = pollStatus;
    }
}
