package gpse.umfrato.domain;

import gpse.umfrato.domain.answer.question.QuestionService;
import gpse.umfrato.domain.answer.user.UserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class InitializeDatabase implements InitializingBean {

    private final UserService userService;
    private final QuestionService questionService;


    @Autowired
    public InitializeDatabase(final UserService userService, QuestionService questionService) {
        this.userService = userService;
        this.questionService = questionService;
    }

    @Override
    public void afterPropertiesSet() {
        try {
            userService.loadUserByUsername("Uncle_Bob");
            questionService.createQuestion("Bist du?");

        } catch (UsernameNotFoundException ex) {
             userService.createUser("Uncle_Bob",
                 "{bcrypt}$2a$10$WoG5Z4YN9Z37EWyNCkltyeFr6PtrSXSLMeFWOeDUwcanht5CIJgPa",
                 "Bob", "Martin", "ROLE_USER");
        }
    }
}
