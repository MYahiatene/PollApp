package gpse.umfrato.domain.cmd;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PollCmd {

    //private String id;

    private String pollcreator;

    //private String pollCreatedAt;

    //private String lastEditAt;

    private String activatedAt;

    private String deactivatedAt;

    private String anonymityStatus;

    private String pollname;

    private int pollStatus;

}
