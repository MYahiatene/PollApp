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

}
