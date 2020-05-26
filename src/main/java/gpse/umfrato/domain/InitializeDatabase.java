package gpse.umfrato.domain;

import gpse.umfrato.domain.answer.AnswerService;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.question.Question;
import gpse.umfrato.domain.question.QuestionService;
import gpse.umfrato.domain.user.UserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
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

    private List<String> generateAnswers(int[] repeats)
    {
        List<String> answers = new ArrayList<>();
        int i = 0;
        for(int r:repeats)
        {
            while(r > 0)
            {
                answers.add(String.valueOf(i));
                r--;
            }
            i++;
        }
        return answers;
    }

    /**
     * This method initializes the database with an user, a poll, a question and an answer.
     */
    @Override
    @Transactional
    public void afterPropertiesSet() {

        final String tbrettmannUserName = "tbrettmann";
        final String testUserName = "testNutzer";
        final String dummyPassword = "{bcrypt}$2a$10$WoG5Z4YN9Z37EWyNCkltyeFr6PtrSXSLMeFWOeDUwcanht5CIJgPa";

        final Poll testPoll = new Poll(tbrettmannUserName, "1", "Umfrage zur IT-Messe 2020", Instant.now().toString(),
            Instant.now().toString(), Instant.now().toString(), 0);

        try {
            userService.loadUserByUsername(tbrettmannUserName);
            //questionService.addQuestion(one, "testFrage", Arrays.asList("Frage1", "Frage2", "Frage3"), "freitext");
            //answerService.giveAnswer(testUsername, one, "3", Arrays.asList("Ja", "Nein"));
        } catch (UsernameNotFoundException e) {
            userService.createUser(tbrettmannUserName, dummyPassword, "Tobias", "Brettmann",
                Arrays.asList("Admin", "User"));
        }

        try {
            userService.loadUserByUsername("testUserName");
        } catch (UsernameNotFoundException ex) {
            userService.createUser("testUserName", dummyPassword, "Markus", "Mueller",
                Arrays.asList("User"));
        }
        generateAnswers(new int[]{1, 2, 3});
        pollService.createPoll(testPoll);
        Question q1 = questionService.addQuestion(testPoll.getPollId().toString(),"Wie hat Ihnen die Veranstaltung insgesamt gefallen?",Arrays.asList("Sehr gut", "Gut", "Überwiegend gut", "Schlecht", "Ich weiß nicht"), "ChoiceQuestion");
        Question q2 = questionService.addQuestion(testPoll.getPollId().toString(),"Welches Geschlecht haben Sie?",Arrays.asList("Weiblich", "Männlich", "Divers"), "ChoiceQuestion");
        Question q3 = questionService.addQuestion(testPoll.getPollId().toString(),"Wie geht es Ihnen heute?",Arrays.asList("Gut", "In Ordnung", "Schlecht"), "ChoiceQuestion");
        Question q4 = questionService.addQuestion(testPoll.getPollId().toString(),"Was hat Sie am Meisten überzeugt?",Arrays.asList("Die Vorträge", "Die Informationsstände", "Das Catering", "Ich kann mich nicht entscheiden"), "ChoiceQuestion");
        Question q5 = questionService.addQuestion(testPoll.getPollId().toString(),"Werden Sie uns nächstes Jahr wieder besuchen?",Arrays.asList("Ja", "Nein", "Vielleicht"), "ChoiceQuestion");
        Question q6 = questionService.addQuestion(testPoll.getPollId().toString(),"Wie viel Zeit haben sie auf der Messe verbracht?",Arrays.asList("unter einer Stunde", "1-2 Stunden", "2-5 Stunden", "über 5 Stunden"), "ChoiceQuestion");
        answerService.giveAnswer(tbrettmannUserName,testPoll.getPollId().toString(),q1.getQuestionId().toString(),generateAnswers(new int[]{70,65,30,5,25}));
        answerService.giveAnswer(tbrettmannUserName,testPoll.getPollId().toString(),q2.getQuestionId().toString(),generateAnswers(new int[]{20,19,1}));
        answerService.giveAnswer(tbrettmannUserName,testPoll.getPollId().toString(),q3.getQuestionId().toString(),generateAnswers(new int[]{22,8,7}));
        answerService.giveAnswer(tbrettmannUserName,testPoll.getPollId().toString(),q4.getQuestionId().toString(),generateAnswers(new int[]{17,8,4,2}));
        answerService.giveAnswer(tbrettmannUserName,testPoll.getPollId().toString(),q5.getQuestionId().toString(),generateAnswers(new int[]{50,21}));
        answerService.giveAnswer(tbrettmannUserName,testPoll.getPollId().toString(),q6.getQuestionId().toString(),generateAnswers(new int[]{12,45,40,20}));
    }
}
