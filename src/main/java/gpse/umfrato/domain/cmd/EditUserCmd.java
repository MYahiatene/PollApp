package gpse.umfrato.domain.cmd;

import lombok.Data;

/**
 * The command design class for editing a user.
 */
@Data
public class EditUserCmd {

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String role;
}
