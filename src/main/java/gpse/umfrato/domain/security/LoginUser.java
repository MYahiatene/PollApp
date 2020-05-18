package gpse.umfrato.domain.security;

public class LoginUser {

    private String username;
    private String password;

    public LoginUser() {

    }

    public LoginUser(String input) {
        username = input.split("&")[0].replace("username=", "");
        password = input.split("&")[1].replace("password=", "");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
