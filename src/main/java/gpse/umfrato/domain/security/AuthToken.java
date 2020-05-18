package gpse.umfrato.domain.security;

public class AuthToken {

    private String token;
    private String username;

    public AuthToken() {

    }

    public AuthToken(final String token, final String username) {
        this.token = token;
        this.username = username;
    }

    public AuthToken(final String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }
}
