package gpse.umfrato.web;

import gpse.umfrato.domain.security.*;
import gpse.umfrato.domain.user.User;
import gpse.umfrato.domain.user.UserService;
import org.apache.commons.logging.Log;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@CrossOrigin
@RestController
@RequestMapping(value = "/token", method = RequestMethod.GET)
public class AuthenticationController {

    private static final Logger LOGGER = Logger.getLogger("AuthenticationController");
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/generate-token")
    public ApiResponse<AuthToken> register(@RequestBody String input) throws AuthenticationException {
        LOGGER.info("register");
        LoginUser loginUser = new LoginUser(input);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword()));
        User user = (User) userService.loadUserByUsername(loginUser.getUsername());
        final String token = jwtTokenUtil.generateToken(user);
        return new ApiResponse<>(200, "success",new AuthToken(token, user.getUsername()));
    }
}
