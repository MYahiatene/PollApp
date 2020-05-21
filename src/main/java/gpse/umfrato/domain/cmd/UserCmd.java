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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
