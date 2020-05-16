package gpse.umfrato.domain;

import gpse.umfrato.domain.answer.AnswerService;

import gpse.umfrato.domain.category.CategoryRepository;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.poll.PollService;

import gpse.umfrato.domain.question.QuestionService;

import gpse.umfrato.domain.user.User;
import gpse.umfrato.domain.user.UserRepository;
import gpse.umfrato.domain.user.UserService;

import org.springframework.beans.factory.InitializingBean;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

@Service
public class InitializeDatabase implements InitializingBean {

    private final UserService userService;

    private final PollService pollService;

    private final QuestionService questionService;

    private final AnswerService answerService;

    private final UserRepository userRepository;

    private final CategoryService categoryService;

    private final CategoryRepository categoryRepository;

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

                              final QuestionService questionService, final AnswerService answerService,
                              final UserRepository userRepository, final CategoryService categoryService,
                              final CategoryRepository categoryRepository) {

        this.userService = userService;

        this.pollService = pollService;

        this.questionService = questionService;

        this.answerService = answerService;
        this.userRepository = userRepository;

        this.categoryRepository = categoryRepository;

        this.categoryService = categoryService;
    }

    /**
     * This method loads a test user in user service and creates a poll.
     */
    @Override
    @Transactional
    public void afterPropertiesSet() {
        try {
            User user = userService.createUser("testUser", "hallo", "test", "heay"
                , new String[]{"Admin", "User"});
            pollService.createPoll("testUser", "anonym", "testPoll", Instant.now().toString()
                , Instant.now().toString(), 0);
            categoryService.addCategory("Keine Kategorie", 1);
            questionService.addQuestion("1", "testFrage", Arrays.asList("Ja", "Nein", "Vielleicht"), "freitext");
            answerService.giveAnswer("testUser", "4", Arrays.asList("Ja", "Nein", "Vielleicht"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
