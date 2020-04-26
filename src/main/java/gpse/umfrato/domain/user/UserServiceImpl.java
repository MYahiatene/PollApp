package gpse.umfrato.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
class UserServiceImpl implements UserService {


    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(final String username, final String password, final String firstname, final String lastname, final String... roles) {
        final User user = new User(username, firstname, lastname, password);
        for (final String role : roles) {
            user.addRole(role);
        }
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return userRepository.findById(username)
            .orElseThrow(() -> new UsernameNotFoundException("User name " + username + " not found."));
    }
}
