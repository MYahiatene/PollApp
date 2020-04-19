package gpse.umfrato.domain;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class InitializeDatabase implements InitializingBean {

    private final UserService userService;

    @Autowired
    public InitializeDatabase(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public void afterPropertiesSet() {
        try {
            userService.loadUserByUsername("Uncle_Bob");
        } catch (UsernameNotFoundException ex) {
            final User user = userService.createUser("Uncle_Bob",
                "{bcrypt}$2a$10$WoG5Z4YN9Z37EWyNCkltyeFr6PtrSXSLMeFWOeDUwcanht5CIJgPa",
                "Bob", "Martin", "ROLE_USER");
        }
    }
}
