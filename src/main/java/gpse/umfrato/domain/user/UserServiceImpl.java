package gpse.umfrato.domain.user;

import gpse.umfrato.domain.mail.MailService;
import gpse.umfrato.domain.password.RandomPasswordGenerator;
import gpse.umfrato.web.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Primary
@Service
class UserServiceImpl implements UserService {

    private static final Logger LOGGER = Logger.getLogger("UserServiceImpl");
    private final UserRepository userRepository;
    private final MailService mailService;

    @Autowired
    public UserServiceImpl(final UserRepository userRepository, final MailService mailService) {
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    /**
     * This method creates a user with every required data.
     *
     * @param role the roles from user
     * @return created user
     */
    // todo: send password form here to user
    @Override
    public User createUser(final String username, final String password, final String firstName,
                           final String lastName, final String role, final String email) {
        final RandomPasswordGenerator randomPasswordGenerator = new RandomPasswordGenerator();
        final User user;
        final List<Long> participated = new ArrayList<>();
        // for a completely fresh user to be generated the password value sent from the frontend must be null
        if (password == null) {
            final char[] safePwd = randomPasswordGenerator.generatePwd();
            final String encryptedSafePwd = PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(
                new String(safePwd));
            user = new User(username, encryptedSafePwd, firstName, lastName, role, email, participated);

            final String pwd = new String(safePwd);
            final String mailSubject = "Login Daten für Umfrato";
            final String mailMessage = "Sehr geehrte/r Frau/Herr " + lastName + ",\n\n"
                + "anbei finden Sie Ihre neuen Login Daten. \n\n" + "Username: " + username + "\n" + "Password: "
                + pwd + "\n\n" + "Mit freundlichen Grüßen\nIhr Umfrato Team";

            mailService.sendMail(mailSubject, mailMessage, email);

            Arrays.fill(safePwd, '*');
        } else {
            user = new User(username, password, firstName, lastName, role, email, participated);
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
     * This method adds a pollId to the partcicipated List of a user.
     *
     * @param username the username of the user
     * @param pollId   pollId of the poll, the user just clicked Absenden on
     */
    @Override
    public void addParticipatedPoll(final String username, final Long pollId) {
        // LOGGER.info("username" + username + "pollId" + pollId);
        final User user = userRepository.getOne(username);
        final List<Long> tmp = user.getParticipated();
        tmp.add(pollId);
        user.setParticipated(tmp);
        // LOGGER.info(tmp.toString());
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
        final String cryptPassword = PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(password);
        user.setPassword(cryptPassword);
        userRepository.save(user);
    }

    @Override
    public void changeEmail(final String username, final String email) {
        final User user = userRepository.getOne(username);
        user.setEmail(email);
        userRepository.save(user);
    }

}
