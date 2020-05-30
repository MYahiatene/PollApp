package gpse.umfrato.domain.cmd;

import lombok.Data;

@Data
public class EditUserCmd {

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String role;
}
