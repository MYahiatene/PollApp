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

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Service
public class InitializeDatabase implements InitializingBean {

    /* default */ final QuestionService questionService;

    /* default */ final AnswerService answerService;

    private final UserService userService;

    private final PollService pollService;

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
     * This method initializes the database with an user, a poll, a question and an answer.
     */
    @Override
    @Transactional
    public void afterPropertiesSet() {

        final String testUsername = "tbrettmann";
        final String dummyPassword = "$2a$10$WoG5Z4YN9Z37EWyNCkltyeFr6PtrSXSLMeFWOeDUwcanht5CIJgPa";


        final Poll testPoll = new Poll(testUsername, "anonym", "testPoll", Instant.now().toString(),
            Instant.now().toString(), Instant.now().toString(), 0);
        final User testUser = new User("testNutzer", dummyPassword, "Markus", "Mueller");
        final User testUserTbrettmann = new User(testUsername, dummyPassword, "Tobias", "Brettmann");

        try {
            userService.loadUserByUsername(testUsername);
            //questionService.addQuestion(one, "testFrage", Arrays.asList("Frage1", "Frage2", "Frage3"), "freitext");
            //answerService.giveAnswer(testUsername, one, "3", Arrays.asList("Ja", "Nein"));
        } catch (UsernameNotFoundException e) {
            userService.createUser(testUserTbrettmann, "ROLE_POLL_CREATOR");
        }

        try {
            userService.loadUserByUsername("testnutzer");
        } catch (UsernameNotFoundException ex) {
            userService.createUser(testUser);
        }

        pollService.createPoll(testPoll);
        Question question = questionService.addQuestion(testPoll.getPollId().toString(),"Ka", Arrays.asList(new String[] { "10", "20", "30", "40" }),"ChoiceQuestion");
        answerService.giveAnswer(testUsername,testPoll.getPollId().toString(),question.getQuestionId().toString(),Arrays.asList(new String[] { "10", "40" }));
    }
}
