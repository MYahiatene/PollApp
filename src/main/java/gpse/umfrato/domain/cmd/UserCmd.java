package gpse.umfrato.domain.cmd;

import gpse.umfrato.domain.user.User;
import lombok.Data;

@Data
public class UserCmd {

    private String username;

    private String firstName;

    private String lastName;

    private String password;

    private String email;

    private String role;
}
