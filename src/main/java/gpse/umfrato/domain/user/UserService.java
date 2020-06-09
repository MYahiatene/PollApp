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
     * @param role      the roles from user
     * @param email the email of the user
     * @return created user
     */
    User createUser(final String username, final String password, final String firstName,
                    final String lastName, final String role, final String email);

    /**
     * This method returns a list with all users.
     *
     * @return list with all users.
     */
    List<User> getAllUsers();

    /**
     * This method edits a user.
     *
     * @param username  the username of the user
     * @param firstName the firstName of the user
     * @param lastName  the lastName of the user
     * @param role      the role of the user
     * @param email     the email of the user
     */
    void editUser(final String username, final String firstName,
                  final String lastName, final String role, final String email);

    /**
     * This method deletes a user.
     *
     * @param username the username of the user
     */
    void deleteUser(final String username);
}
