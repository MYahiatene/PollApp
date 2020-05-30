package gpse.umfrato.web;

import gpse.umfrato.domain.cmd.UserCmd;
import gpse.umfrato.domain.user.User;
import gpse.umfrato.domain.user.UserRepository;
import gpse.umfrato.domain.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@RequestMapping(value = "/api")
@RestController
@CrossOrigin
public class UserController {
    private static final Logger LOGGER = Logger.getLogger("UserController");
    private final UserService userService;
    private final UserRepository userRepository;

    /**
     * This class constructor initializes the user service and user repository.
     *
     * @param userService    the user service
     * @param userRepository the user repository
     */
    @Autowired
    public UserController(final UserService userService, final UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    /**
     * This method creates a user.
     *
     * @param userCmd has the user data to create an object user
     * @return a string that method was called
     */
    @PreAuthorize("hasAuthority('Admin')")
    @PostMapping("/createuser")
    public String createUser(final @RequestBody UserCmd userCmd) {
        try {
            userService.createUser(
                userCmd.getUsername(), userCmd.getPassword(), userCmd.getFirstName(), userCmd.getLastName(),
                userCmd.getRole(),userCmd.getEmail());

        } catch (BadRequestException e) {
            LOGGER.info("couldnt create user");
        }
        return "HTTP POST was called";
    }

    /**
     * This method returns a list with all users.
     *
     * @return a list with all users
     */
    @PreAuthorize("hasAuthority('Admin')")
    @GetMapping("/users")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    /**
     * This method returns a requested user.
     *
     * @param usercmd has the username of the requested user
     * @return the requested user
     */
    @PreAuthorize("hasAuthority('Admin')")
    @GetMapping("/getuser/{userId:\\d+}")
    public String getUser(final @RequestBody UserCmd usercmd) {
        return userService.loadUserByUsername(usercmd.getUsername()).getUsername();
    }
    @PreAuthorize("hasAuthority('Admin')")
    @GetMapping("/checkToken")
        public void checkToken(){

    }
}
