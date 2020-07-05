package gpse.umfrato.domain.cmd;

import lombok.Data;

/**
 * The command design class for deleting a user.
 */
@Data
public class ParticipatedUserCmd {

    private String username;
    private Long pollId;

}
