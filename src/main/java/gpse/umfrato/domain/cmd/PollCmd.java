package gpse.umfrato.domain.cmd;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PollCmd {

    private String id;

    private String pollcreator;

    private LocalDateTime pollCreatedAt;

    private LocalDateTime lastEditAt;

    private LocalDateTime activatedAt;

    private LocalDateTime deactivatedAt;

    private String anonymityStatus;

    private String pollname;

    private int pollStatus;

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
