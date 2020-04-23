package gpse.umfrato.web;

import gpse.umfrato.domain.User;
import gpse.umfrato.domain.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RequestMapping("/api")
@RestController
@CrossOrigin
public class UserController {
    private UserService userService;

    private static final Logger LOGGER = Logger.getLogger("UserController");

    @Autowired
    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/create")
    public void creatUser() {
        try {
            userService.createUser("Richie", "Richard", "Hübert", "password");

        } catch (Exception e) {
            LOGGER.info("couldnt create user");
        }
    }

    @GetMapping("/users")
    public User getUser() {
        final User user = new User("Richie", "Richard", "Hübert", "password");
        return user;
    }
}
