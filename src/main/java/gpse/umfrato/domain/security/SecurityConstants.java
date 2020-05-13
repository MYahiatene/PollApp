package gpse.umfrato.domain.security;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("security") //<1>
final class SecurityConstants {

    private String authLoginUrl; //<2>

    // Signing key für den HS512-Algorithm
    // Eigene Schlüssel könnt ihr unter http://www.allkeysgenerator.com/ erstellen lassen.
    private String jwtSecret; //<3>

    // JWT Token-Standardwerte
    private String tokenHeader; //<4>
    private String tokenPrefix; //<5>
    private String tokenType; //<6>
    private String tokenIssuer; //<7>
    private String tokenAudience; //<8>

    public String getJwtSecret() {
        return jwtSecret;
    }

    public String getTokenHeader() {
        return tokenHeader;
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }

    public void setTokenHeader(String tokenHeader) {
        this.tokenHeader = tokenHeader;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getTokenIssuer() {
        return tokenIssuer;
    }

    public void setTokenIssuer(String tokenIssuer) {
        this.tokenIssuer = tokenIssuer;
    }

    public String getTokenAudience() {
        return tokenAudience;
    }

    public void setTokenAudience(String tokenAudience) {
        this.tokenAudience = tokenAudience;
    }

    public String getAuthLoginUrl() {
        return authLoginUrl;
    }

    public void setAuthLoginUrl(String authLoginUrl) {
        this.authLoginUrl = authLoginUrl;
    }

    // Getter und Setter für die Attribute <9>
}
