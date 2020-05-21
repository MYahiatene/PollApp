package gpse.umfrato.domain.user;

import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    /**
     * This method creates a user with every required data.
     *
     * @param user  the user who should be created
     * @param roles the roles from user
     * @return created user
     */
    User createUser(User user, String... roles);

    /**
     * This method returns a list with all users.
     *
     * @return list with all users.
     */
    List<User> getAllUsers();
}
