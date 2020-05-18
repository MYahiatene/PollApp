package gpse.umfrato.domain.security;

import gpse.umfrato.domain.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {

private static final long serialVersionUID = 1L;
public static final int TEN_DAYS_IN_MILLISECONDS = 864000000;

public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
            .setSigningKey(Constants.JWT_SECRET.getBytes())
            .parseClaimsJws(token)
            .getBody();
    }

    private Boolean isTokenExpired(final String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(final User user) {
        return doGenerateToken(user);
    }

    private String doGenerateToken(final User user) {
        return Jwts.builder()
            .signWith(Keys.hmacShaKeyFor(Constants.JWT_SECRET.getBytes()), SignatureAlgorithm.HS512)
            .setHeaderParam("typ", Constants.TOKEN_TYPE)
            .setIssuer(Constants.TOKEN_ISSUER)
            .setAudience(Constants.TOKEN_AUDIENCE)
            .setSubject(user.getUsername())
            .setExpiration(new Date(System.currentTimeMillis() + TEN_DAYS_IN_MILLISECONDS))
            .claim("rol", user.getRoles())
            .compact();
    }

    public Boolean validateToken(final String token, final UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (
            username.equals(userDetails.getUsername())
                && !isTokenExpired(token));
    }

}
