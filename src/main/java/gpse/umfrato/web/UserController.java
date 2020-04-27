package gpse.umfrato.web;

import gpse.umfrato.domain.cmd.UserCmd;
import gpse.umfrato.domain.user.User;
import gpse.umfrato.domain.user.UserRepository;
import gpse.umfrato.domain.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RequestMapping(value = "/api", method = RequestMethod.GET)
@RestController
@CrossOrigin
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    private static final Logger LOGGER = Logger.getLogger("UserController");

    @Autowired
    public UserController(final UserService userService,final UserRepository userRepository) {
        this.userService = userService;
        this.userRepository=userRepository;
    }

    @PostMapping("/createuser")
    public String createUser(@RequestBody UserCmd userCmd) {
        try {
            userService.createUser(userCmd.getUsername(), userCmd.getPassword(), userCmd.getFirstname(), userCmd.getLastname());

        } catch (Exception e) {
            LOGGER.info("couldnt create user");
        }
        return "HTTP POST was called";
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/getuser/{userId:\\d+}")
    public String getUser(@RequestBody UserCmd usercmd) {
        return userService.loadUserByUsername(usercmd.getUsername()).getUsername();
    }
}
