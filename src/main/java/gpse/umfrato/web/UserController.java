package gpse.umfrato.web;

import gpse.umfrato.domain.cmd.DeleteUserCmd;
import gpse.umfrato.domain.cmd.EditUserCmd;
import gpse.umfrato.domain.cmd.UserCmd;
import gpse.umfrato.domain.password.RandomPasswordGenerator;
import gpse.umfrato.domain.user.User;
import gpse.umfrato.domain.user.UserRepository;
import gpse.umfrato.domain.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

/**
 * The User controller used to process user specific requests.
 */
@RequestMapping("/api")
@RestController
@CrossOrigin
public class UserController {
    private static final Logger LOGGER = Logger.getLogger("UserController");
    private static final String HTTP_POST = "HTTP POST was called";
    /* default */ final UserRepository userRepository;
    private final UserService userService;

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
    @PostMapping("/createUser")
    public String createUser(final @RequestBody UserCmd userCmd) {
        try {
            userService.createUser(
                userCmd.getUsername(), userCmd.getPassword(), userCmd.getFirstName(), userCmd.getLastName(),
                userCmd.getRole(), userCmd.getEmail());

        } catch (BadRequestException e) {
            LOGGER.info("Could not create user");
        }
        return HTTP_POST;
    }

    /**
     * This method edits user details.
     *
     * @param editUserCmd the Cmd includes all necessary details
     * @return returns a confirmation String
     */
    @PreAuthorize("hasAuthority('Admin')")
    @PutMapping("editUser")
    public String editUser(final @RequestBody EditUserCmd editUserCmd) {
        LOGGER.info("UserCmd" + editUserCmd);
        try {
            userService.editUser(editUserCmd.getUsername(), editUserCmd.getFirstName(), editUserCmd.getLastName(),
                editUserCmd.getRole(), editUserCmd.getEmail());
        } catch (BadRequestException e) {
            LOGGER.info("Could not edit user");
        }
        return HTTP_POST;
    }

    /**
     * This method returns a list with all users.
     *
     * @return a list with all users
     */
    @PreAuthorize("hasAuthority('Admin')")
    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/usersNonAdmin")
    public List<User> getUsersNonAdmin() {
        return userService.getAllUsers();
    }

    /**
     * This method returns a requested user.
     *
     * @param usercmd has the username of the requested user
     * @return the requested user
     */
    @PreAuthorize("hasAuthority('Admin')")
    @GetMapping("/getuser")
    public String getUser(final @RequestBody UserCmd usercmd) {
        return userService.loadUserByUsername(usercmd.getUsername()).getUsername();
    }

    /**
     * Checks if the users token has the authority admin.
     */
    @PreAuthorize("hasAuthority('Admin')")
    @GetMapping("/checkToken")
    public void checkToken() {

    }

    /**
     * Checks if the users token has the authority "Creator".
     */
    @PreAuthorize("hasAuthority('Creator')")
    @GetMapping("/checkCreatorToken")
    public void checkCreatorToken() {

    }

    /**
     * Deletes a user in the data base.
     *
     * @param deleteUserCmd the user to delete
     */
    @PreAuthorize("hasAuthority('Admin')")
    @PutMapping("/deleteUser")
    public String deleteUser(final @RequestBody DeleteUserCmd deleteUserCmd) {
        userService.deleteUser(deleteUserCmd.getUsername());
        return "test";
    }

    /**
     * Changes the password in the database.
     *
     * @param userCmd
     * @return 1 or 0; depending if it workes or not
     */
    @PutMapping("/changePassword")
    public int changePassword(final @RequestBody UserCmd userCmd) {
        LOGGER.info("UserCmd" + userCmd);
        try{
            userService.changePassword(userCmd.getUsername(), userCmd.getPassword());
            return 1;
        }
        catch(BadRequestException e){
            LOGGER.info("Could not Change Password");
            return 0;
        }
    }

    @PutMapping("/changeEmail")
    public void changeEmail(final @RequestBody UserCmd userCmd) {
        // userCmd.email is here null... Why?
        LOGGER.info("UserCmd" + userCmd);
        try{
            userService.changeEmail(userCmd.getUsername(), userCmd.getEmail());
        }
        catch(BadRequestException e){
            LOGGER.info("Could not Change Email");
        }
    }

    // todo: test function only
    @PreAuthorize("hasAuthority('Admin')")
    @PostMapping("/generatepwd")
    public String generatePwd() {
        final RandomPasswordGenerator randomPasswordGenerator = new RandomPasswordGenerator();
        return new String(randomPasswordGenerator.generatePwd());
    }
}
