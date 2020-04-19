package gpse.umfrato.domain;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User createUser(String username, String password, String firstname, String lastname, String... roles);
}
