package gpse.umfrato.domain.security;

public class LoginUser {

    private String username;
    private String password;

    public LoginUser() {

    }

    public LoginUser(final String input) {
        username = input.split("&")[0].replace("username=", "");
        password = input.split("&")[1].replace("password=", "");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }
}
