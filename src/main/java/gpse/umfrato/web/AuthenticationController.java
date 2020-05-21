package gpse.umfrato.web;

import gpse.umfrato.domain.security.*;
import gpse.umfrato.domain.user.User;
import gpse.umfrato.domain.user.UserService;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/token", method = RequestMethod.GET)
public class AuthenticationController {

    private static final int STATUS_OK = 200;
    private static final Logger LOGGER = Logger.getLogger("AuthenticationController");
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/generate-token")
    public ApiResponse<AuthToken> register(@RequestBody final String input) throws AuthenticationException {
        LOGGER.info("register");
        final LoginUser loginUser = new LoginUser(input);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUser.getUsername(),
            loginUser.getPassword()));
        final User user = (User) userService.loadUserByUsername(loginUser.getUsername());
        final String token = jwtTokenUtil.generateToken(user);
        return new ApiResponse<>(STATUS_OK, "success", new AuthToken(token, user.getUsername()));
    }
}
