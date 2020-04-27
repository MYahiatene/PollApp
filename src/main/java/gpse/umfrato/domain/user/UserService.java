package gpse.umfrato.domain.user;

import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    /**
     * This method creates a user with every required data.
     *
     * @param username  the user name from user
     * @param password  the password from user
     * @param firstname the first name from user
     * @param lastname  the last name from user
     * @param roles     the roles from user
     * @return created user
     */
    User createUser(String username, String password, String firstname, String lastname, String... roles);

    /**
     * This method returns a list with all users.
     *
     * @return list with all users.
     */
    List<User> getAllUsers();
}
