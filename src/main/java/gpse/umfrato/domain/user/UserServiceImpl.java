package gpse.umfrato.domain.user;

import gpse.umfrato.web.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class UserServiceImpl implements UserService {


    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
    @Override
    public User createUser(final String username, final String password, final String firstname, final String lastname,
                           final String... roles) {
        final User user = new User(username, firstname, lastname, password);
        for (final String role : roles) {
            user.addRole(role);
        }
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
     * This method search for a user with username.
     *
     * @param username the username of the requested user
     * @return the requested user
     */
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return userRepository.findById(username)
            .orElseThrow(() -> new UsernameNotFoundException("User name " + username + " not found."));
    }
}
