package gpse.umfrato.domain.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.*;

import java.util.Arrays;

/**
 * This class specifies our security config.
 */
@Configuration
@EnableWebSecurity
@EnableAutoConfiguration
@EnableConfigurationProperties(SecurityConstants.class)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final SecurityConstants securityConstants;

    /**
     * The constructor takes our security constants(for jwt).
     *
     * @param securityConstants takes our security constants.
     */
    @Autowired
    public SecurityConfig(final SecurityConstants securityConstants) {
        this.securityConstants = securityConstants;
    }

    /**
     * This method configures our security access(csrf,cors,authorities,session management).
     *
     * @param http takes the http security object.
     * @throws Exception throws a generic Exception.
     */
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
            .authorizeRequests()
            .antMatchers("/api/**").hasAnyAuthority("Admin", "Creator", "Editor", "User")
            .and()
            .addFilter(new JwtAuthenticationFilter(authenticationManager(), securityConstants))
            .addFilter(new JwtAuthorizationFilter(authenticationManager(), securityConstants))
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // this enables h2 console for debugging purposes(bad for security)
        http.headers().frameOptions().disable();
    }

    /**
     * This method sets our password encoder.
     *
     * @param userService     takes user service to use user specific methods.
     * @param passwordEncoder takes the specific password encoder.
     * @param auth            takes auth object to set password encoder.
     * @throws Exception throws generic exception.
     */
    @Autowired
    public void configureGlobal(final UserDetailsService userService,
                                final PasswordEncoder passwordEncoder,
                                final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
            .passwordEncoder(passwordEncoder);

    }

    /**
     * This method sets our password encoder to delegating password encoder.
     *
     * @return returns the specific password encoder object.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * This method sets our cors settings for different requests.
     *
     * @return returns our cors config surce object.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        corsConfiguration.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "PATCH", "DELETE"));
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }
}
