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

        String mockData = "{\"name\":\"Umfrage zur IT-Messe 2020\",\"questionList\": [{\"id\": 1,\"title\": \"Wie hat" +
            " Ihnen die Veranstaltung insgesamt gefallen?\",\"answerPossibilities\": [\"Sehr gut\",\"Gut\",\"Überwieg" +
            "end gut\",\"Schlecht\",\"Ich weiß nicht\"],\"data\": [70,65,30,5,25]},{\"id\": 2,\"title\": \"Welches Ge" +
            "schlecht haben Sie?\",\"answerPossibilities\": [\"Weiblich\",\"Männlich\",\"Divers\"],\"data\": [20,19,1" +
            "]},{\"id\": 3,\"title\": \"Wie geht es Ihnen heute?\",\"answerPossibilities\": [\"Gut\",\"In Ordnung\"," +
            "\"Schlecht\"],\"data\": [22,8,7]},{\"id\": 4,\"title\": \"Was hat Sie am Meisten überzeugt?\",\"answerPo" +
            "ssibilities\": [\"Die Vorträge\",\"Die Informationsstände\",\"Das Catering\",\"Ich kann mich nicht entsc" +
            "heiden\"],\"data\": [17,8,4,2]},{\"id\": 5,\"title\": \"Werden Sie uns nächstes Jahr wieder besuchen?\"," +
            "\"answerPossibilities\": [\"Ja\",\"Nein\"],\"data\": [50,21]},{\"id\": 6,\"title\": \"Wie viel Zeit habe" +
            "n sie auf der Messe verbracht?\",\"answerPossibilities\": [\"unter einer Stunde\",\"1-2 Stunden\",\"2-5 " +
            "Stunden\",\"über 5 Stunden\"],\"data\": [12,45,40,20]}]}";

        final Poll testPoll = new Poll(testUsername, "anonym", "testPoll", Instant.now().toString(),
            Instant.now().toString(), Instant.now().toString(), 0);
        final User testUserTbrettmann = new User(testUsername, dummyPassword, "Tobias", "Brettmann");
        final User testUser = new User("testNutzer", dummyPassword, "Markus", "Mueller");

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

    }
}
