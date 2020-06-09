package gpse.umfrato.domain;

import gpse.umfrato.domain.answer.AnswerService;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.question.QuestionService;
import gpse.umfrato.domain.user.User;
import gpse.umfrato.domain.user.UserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import javax.transaction.Transactional;
import java.time.Instant;

@Service
public class InitializeDatabase implements InitializingBean {

    static final String TESTNUTZER = "testNutzer";
    static final String MARKUS = "Markus";
    static final String MUELLER = "Mueller";
    static final String ADMIN = "Admin";
    static final String TOBIAS = "Tobias";
    static final String ONE = "1";
    static final String CHOICEQUESTION = "choiceQuestion";

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
        final String logoUrl = "https://picsum.photos/510/300?random";
        final String dummyPassword = "{bcrypt}$2a$10$WoG5Z4YN9Z37EWyNCkltyeFr6PtrSXSLMeFWOeDUwcanht5CIJgPa";


        final Poll testPoll = new Poll(testUsername, "anonym", "testPoll", Instant.now().toString(),
            Instant.now().toString(), Instant.now().toString(), 0, "#c4fcdb", "#c42843",
            logoUrl, true, true);
        final User testUser = new User(TESTNUTZER, dummyPassword, MARKUS, MUELLER, ADMIN, "testuser@testmail.de");
        final User testUserTbrettmann = new User(testUsername, dummyPassword, TOBIAS, "Brettmann", ADMIN,
            "tobias.bettmann@mail.com");

        try {
            pollService.createPoll(testPoll);

            questionService.addQuestion(ONE, "testFrage", Arrays.asList("Ja", "Nein", "Vielleicht"), CHOICEQUESTION);
            questionService.addQuestion(ONE, "testFrage2", Arrays.asList("Jein", "Fein", "Vielschwer"), CHOICEQUESTION);
            questionService.addQuestion(ONE, "testFrage3", new ArrayList<>(), "TextQuestion");
            questionService.addQuestion(ONE, "TestFrage 4", new ArrayList<>(), "RangeQuestion");

            userService.loadUserByUsername(testUsername);
//            final List<Question> questions = new ArrayList<>(); // später import Arraylist löschen
//            questions.add(new Question("Wie ist das Wetter heute?"));
//            questions.add(new Question("Wie war dein Tag?"));
//            questions.add(new Question("Come ti chiami?"));
            // questionService.addQuestion("1", "testFrage", Arrays.asList("Ja", "Nein", "Vielleicht"), "choicebox");
            //answerService.giveAnswer(testUsername, one, "3", Arrays.asList("Ja", "Nein"));
        } catch (UsernameNotFoundException e) {
            userService.createUser(testUsername, dummyPassword, TOBIAS, "Bettmann",
                ADMIN, "tbettmann@reply.de");
        }

        try {
            userService.loadUserByUsername(TESTNUTZER);
        } catch (UsernameNotFoundException ex) {
            userService.createUser(TESTNUTZER, dummyPassword, MARKUS, MUELLER,
                "Teilnehmer", "mmueller@gmx.de");
        }

    }
}
