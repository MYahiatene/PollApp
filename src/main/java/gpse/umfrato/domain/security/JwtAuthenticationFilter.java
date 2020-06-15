package gpse.umfrato.domain.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final SecurityConstants securityConstants;

    public JwtAuthenticationFilter(final AuthenticationManager authenticationManager,
                                   final SecurityConstants securityConstants) {
        this.authenticationManager = authenticationManager;
        this.securityConstants = securityConstants;

        setFilterProcessesUrl(this.securityConstants.getAuthLoginUrl());
    }

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response) {
        final String username = request.getParameter("username");
        final String password = request.getParameter("password");
        final UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response,
                                            final FilterChain filterChain, final Authentication authentication)
        throws IOException {
        final UserDetails user = (UserDetails) authentication.getPrincipal();

        final List<String> roles = user.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

        final byte[] signingKey = securityConstants.getJwtSecret().getBytes();

        final String token = Jwts.builder()
            .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
            .setHeaderParam("typ", securityConstants.getTokenType())
            .setIssuer(securityConstants.getTokenIssuer())
            .setAudience(securityConstants.getTokenAudience())
            .setSubject(user.getUsername())
            .setExpiration(new Date(System.currentTimeMillis() + 864_000_000))
            .claim("rol", roles)
            .compact();
        final ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(mapper.writeValueAsString(token));
        response.getWriter().flush();
        //response.setHeader(securityConstants.getTokenHeader(), securityConstants.getTokenPrefix() + token);
        //response.addHeader(securityConstants.getTokenHeader(), securityConstants.getTokenPrefix() + token);
    }
}




