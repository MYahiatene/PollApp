package gpse.umfrato.domain.security;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * This class defines our security constants variables(declared in application properties for security reasons).
 */
@Data
@NoArgsConstructor
@ConfigurationProperties("security")
public final class SecurityConstants {

    private String authLoginUrl;

    private String jwtSecret;

    private String tokenHeader;
    private String tokenPrefix;
    private String tokenType;
    private String tokenIssuer;
    private String tokenAudience;

}
