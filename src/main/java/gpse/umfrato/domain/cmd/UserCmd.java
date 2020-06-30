package gpse.umfrato.domain.cmd;

import lombok.Data;

/**
 * The command design class for user.
 */
@Data
public class UserCmd {

    private String username;

    private String firstName;

    private String lastName;

    private String password;

    private String email;

    private String role;
}
