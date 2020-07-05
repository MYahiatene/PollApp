package gpse.umfrato.domain.cmd;

import lombok.Data;

import java.util.List;

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

    private List<Long> participated;
}
