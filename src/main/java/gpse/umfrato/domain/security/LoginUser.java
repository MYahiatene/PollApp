package gpse.umfrato.domain.security;

public class LoginUser {

    private static final String AND = "&";
    private String username;
    private String password;

    public LoginUser() {

    }

    public LoginUser(final String input) {
        username = input.split(AND)[0].replace("username=", "");
        password = input.split(AND)[1].replace("password=", "");
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
