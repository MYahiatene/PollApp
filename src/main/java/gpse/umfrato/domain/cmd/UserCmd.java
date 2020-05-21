package gpse.umfrato.domain.cmd;

import gpse.umfrato.domain.user.User;
import lombok.Data;

@Data
public class UserCmd {

    private String username;

    private String firstName;

    private String lastName;

    private String password;

    public User getCmdUser() {
        final User user = new User(username, password, firstName, lastName);
        return user;
    }
}
