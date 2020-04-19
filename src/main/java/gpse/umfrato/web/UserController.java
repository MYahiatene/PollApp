package gpse.umfrato.web;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import gpse.umfrato.domain.User;
import gpse.umfrato.domain.UserRepository;
import gpse.umfrato.domain.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
/*
    @PutMapping("/create")
    public void creatUser() {
        try {
            userService.createUser("Richie", "Richard", "Hübert", "password");

        } catch (Exception e) {
        }
    }
*/
    @GetMapping("/users")
    public User getUser() {
        User user = new User("Richie", "Richard", "Hübert", "password");
        return user;
    }
}
