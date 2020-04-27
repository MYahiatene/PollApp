package gpse.umfrato.domain.user;

import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User createUser(String username, String password, String firstname, String lastname, String... roles);

    List<User> getAllUsers();
}
