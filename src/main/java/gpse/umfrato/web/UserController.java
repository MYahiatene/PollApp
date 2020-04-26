package gpse.umfrato.web;

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
    private UserService userService;
    private final UserRepository userRepository;

    private static final Logger LOGGER = Logger.getLogger("UserController");

    @Autowired
    public UserController(final UserService userService,final UserRepository userRepository) {
        this.userService = userService;
        this.userRepository=userRepository;
    }

    @PostMapping("/createuser")
    public String createUser() {
        try {
            userService.createUser("Richie", "Richard", "Hübert", "password");

        } catch (Exception e) {
            LOGGER.info("couldnt create user");
        }
        return "HTTP POST was called";
    }

    @GetMapping("/users")
    public List<User> getUser() {
         return userRepository.findAll();

    }
}
