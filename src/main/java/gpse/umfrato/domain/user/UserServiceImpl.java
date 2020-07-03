package gpse.umfrato.domain.user;

import gpse.umfrato.web.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Primary
@Service
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private static final Logger LOGGER = Logger.getLogger("UserServiceImpl");

    @Autowired
    public UserServiceImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * This method creates a user with every required data.
     *
     * @param role the roles from user
     * @return created user
     */
    @Override
    public User createUser(final String username, final String password, final String firstName,
                           final String lastName, final String role, final String email) {
        final User user = new User(username, password, firstName, lastName, role, email);
        return userRepository.save(user);
    }

    /**
     * This method returns a list with all users.
     *
     * @return list with all users.
     */
    @Override
    public List<User> getAllUsers() {
        final List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            throw new BadRequestException();
        }

        return users;
    }

    /**
     * This method edits the user in the repository.
     *
     * @param username  the username of the user
     * @param firstName the firstName of the user
     * @param lastName  the lastName of the user
     * @param role      the role of the user
     * @param email     the email of the user
     */
    @Override
    public void editUser(final String username, final String firstName, final String lastName, final String role,
                         final String email) {
        final User user = userRepository.getOne(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        final List<String> tmp = new ArrayList<>();
        tmp.add(role);
        user.setRoles(tmp);
        user.setEmail(email);
        userRepository.save(user);
    }

    /**
     * This method deletes the user in the repository based on the id username.
     *
     * @param username the username of the user
     */
    @Override
    public void deleteUser(final String username) {
        userRepository.delete(userRepository.getOne(username));
    }

    /**
     * This method search for a user with username.
     *
     * @param username the username of the requested user
     * @return the requested user
     */
    @Override
    public UserDetails loadUserByUsername(final String username) {
        return userRepository.findById(username)
            .orElseThrow(() -> new UsernameNotFoundException("Username " + username + " not found."));
    }

    @Override
    public void changePassword(final String username, final String password) {
        final User user = userRepository.getOne(username);
        LOGGER.info("username, password" + username + password);
        String cryptPassword = PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(password);
        LOGGER.info(cryptPassword);
        user.setPassword(cryptPassword);
        userRepository.save(user);
        LOGGER.info(userRepository.getOne(username).getPassword());
        LOGGER.info("All done in UserServiceImpl");
    }

    @Override
    public void changeEmail(final String username, final String email) {
        final User user = userRepository.getOne(username);
        user.setEmail(email);
        userRepository.save(user);
        LOGGER.info("Changed Password!");
    }

}
