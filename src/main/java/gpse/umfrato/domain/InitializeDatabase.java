package gpse.umfrato.domain;

import gpse.umfrato.domain.answer.AnswerService;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.question.Question;
import gpse.umfrato.domain.question.QuestionService;
import gpse.umfrato.domain.user.User;
import gpse.umfrato.domain.user.UserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class InitializeDatabase implements InitializingBean {

    private final UserService userService;
    private final PollService pollService;
    private final QuestionService questionService;
    private final AnswerService answerService;


    @Autowired
    public InitializeDatabase(final UserService userService, final PollService pollService,
                              final QuestionService questionService, final AnswerService answerService) {
        this.userService = userService;
        this.pollService = pollService;
        this.questionService = questionService;
        this.answerService = answerService;

    }

    @Override
    public void afterPropertiesSet() {
        try {
            User user = (User) userService.loadUserByUsername("Uncle_Bob");
            Poll poll = pollService.createPoll("Erste Umfrage");





        } catch (UsernameNotFoundException ex) {
            userService.createUser("Uncle_Bob",
                "{bcrypt}$2a$10$WoG5Z4YN9Z37EWyNCkltyeFr6PtrSXSLMeFWOeDUwcanht5CIJgPa",
                "Bob", "Martin", "ROLE_USER");
        }

    }
}
