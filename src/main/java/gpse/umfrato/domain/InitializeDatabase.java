package gpse.umfrato.domain;

import gpse.umfrato.domain.answer.AnswerService;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.question.QuestionService;
import gpse.umfrato.domain.user.UserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class InitializeDatabase implements InitializingBean {

    private static final String TEST_USER = "Uncle_Bob";

    private final UserService userService;
    private final PollService pollService;
    private final QuestionService questionService;
    private final AnswerService answerService;


    /**
     * This method initializes the database.
     *
     * @param userService     the object user service
     * @param pollService     the object poll service
     * @param questionService the object question service
     * @param answerService   the object answer service
     */
    @Autowired
    public InitializeDatabase(final UserService userService, final PollService pollService,
                              final QuestionService questionService, final AnswerService answerService) {
        this.userService = userService;
        this.pollService = pollService;
        this.questionService = questionService;
        this.answerService = answerService;

    }

    /**
     * This method loads a test user in user service and creates a poll.
     */
    @Override
    public void afterPropertiesSet() {
        try {
            userService.loadUserByUsername(TEST_USER);
            pollService.createPoll("Erste Umfrage", TEST_USER, "true", 0);


        } catch (UsernameNotFoundException ex) {
            userService.createUser(TEST_USER,
                "{bcrypt}$2a$10$WoG5Z4YN9Z37EWyNCkltyeFr6PtrSXSLMeFWOeDUwcanht5CIJgPa",
                "Bob", "Martin", "ROLE_USER");
        }

    }
}
