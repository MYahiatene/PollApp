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
     * @param role      the roles of the user
     * @param email     the email of the user
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
     * This method adds a pollId to the partcicipated List of a user
     *
     * @param username the username of the user
     * @param pollId pollId of the poll, the user just clicked Absenden on
     */
    void addParticipatedPoll(final String username, final Long pollId);

    /**
     * This method deletes a user.
     *
     * @param username the username of the user
     */
    void deleteUser(final String username);

    /**
     * This method changes a password.
     *
     * @param password
     */
    void changePassword(final String username, final String password);

    /**
     * This method changes the email of a  user.
     *
     * @param username
     * @param email
     */
    void changeEmail(final String username, final String email);
}
