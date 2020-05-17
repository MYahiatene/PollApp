package gpse.umfrato.domain;

import gpse.umfrato.domain.answer.AnswerService;

import gpse.umfrato.domain.category.CategoryRepository;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.poll.PollService;

import gpse.umfrato.domain.question.QuestionService;

import gpse.umfrato.domain.user.UserRepository;
import gpse.umfrato.domain.user.UserService;

import org.springframework.beans.factory.InitializingBean;

import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Arrays;

@Service
public class InitializeDatabase implements InitializingBean {

    private static final String TEST_USER = "testUser";

    private static final String ONE = "1";

    private static final String YES = "Ja";

    private static final String NO = "Nein";

    private final UserService userService;

    private final PollService pollService;

    private final QuestionService questionService;

    private final AnswerService answerService;

     /* default */ final UserRepository userRepository;

    /* default */ final CategoryService categoryService;

    /* default */ final CategoryRepository categoryRepository;

    /**
     * This method initializes the database.
     *
     * @param userService        the object user service
     * @param pollService        the object poll service
     * @param questionService    the object question service
     * @param answerService      the object answer service
     * @param userRepository     the user repository with all users
     * @param categoryService    the object category service
     * @param categoryRepository the category repository with all categories
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
            userService.createUser(TEST_USER, "hallo", "test", "heay", "Admin", "User");
            pollService.createPoll(TEST_USER, "anonym", "testPoll", Instant.now().toString(),
                Instant.now().toString(), Instant.now().toString(), 0);
            questionService.addQuestion(ONE, "testFrage", Arrays.asList(YES, NO, "Vielleicht"),
                "freitext");
            answerService.giveAnswer(TEST_USER, ONE, "3", Arrays.asList(YES, NO));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
