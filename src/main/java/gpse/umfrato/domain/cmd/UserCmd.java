package gpse.umfrato.domain.cmd;

import lombok.Data;

@Data
public class UserCmd {

    private String username;

    private String firstName;

    private String lastName;

    private String password;

    private String email;
}
