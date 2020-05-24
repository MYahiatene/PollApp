package gpse.umfrato.domain.user;

import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    /**
     * This method creates a user with every required data.
     *
     * @param username  the username of the user
     * @param password  the passwor of the user
     * @param firstName first name of the user
     * @param lastName  last name of the user
     * @param roles     the roles from user
     * @return created user
     */
    User createUser(final String username, final String password, final String firstName,
                    final String lastName, final List<String> roles);

    /**
     * This method returns a list with all users.
     *
     * @return list with all users.
     */
    List<User> getAllUsers();
}
