package gpse.umfrato.domain;

import gpse.umfrato.domain.category.CategoryService;
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
import java.util.Arrays;
import java.util.Collections;

@Service
public class InitializeDatabase implements InitializingBean {

    /* default */ final QuestionService questionService;

    /* default */ final AnswerService answerService;

    private final UserService userService;

    private final PollService pollService;

    private final CategoryService categoryService;

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
                              final CategoryService categoryService) {

        this.userService = userService;

        this.pollService = pollService;

        this.questionService = questionService;

        this.answerService = answerService;

        this.categoryService = categoryService;
    }

    /**
     * This method initializes the database with an user, a poll, a question and an answer.
     */
    @Override
    @Transactional
    public void afterPropertiesSet() {

        final String tbettmannUserName = "tbettmann";
        final String dummyPassword = "{bcrypt}$2a$10$WoG5Z4YN9Z37EWyNCkltyeFr6PtrSXSLMeFWOeDUwcanht5CIJgPa";


        final Poll testPoll = new Poll(tbettmannUserName, "anonym", "Umfrage IT-Messe 2020", Instant.now().toString(),
            Instant.now().toString(), Instant.now().toString(), 0);

        userService.createUser(tbettmannUserName, dummyPassword, "Tobias", "Bettmann",
            "Admin", "tbettmann@reply.de");

        userService.createUser("testNutzer", dummyPassword, "Markus", "Mueller",
            "Teilnehmer", "mmueller@gmx.de");

        final Poll poll = pollService.createPoll(testPoll);
        categoryService.createCategory("Testkategorie aus InitDB", poll.getPollId());
        questionService.addQuestion(String.valueOf(poll.getPollId()), "Testfrage aus InitDB", Arrays.asList("Testantwort aus InitDB","Andere Antwort aus InitDB"), "TestType aus InitDB");
        /* Question q1 = questionService.addQuestion(poll.getPollId().toString(), "Wie hat Ihnen die Veranstaltung insgesamt gefallen?", Arrays.asList("Sehr gut", "Gut", "Überwiegend gut", "Schlecht", "Ich weiß nicht"), "ChoiceQuestion");
        Question q2 = questionService.addQuestion(testPoll.getPollId().toString(), "Welches Geschlecht haben Sie?", Arrays.asList("Weiblich", "Männlich", "Divers"), "ChoiceQuestion");
        Question q3 = questionService.addQuestion(testPoll.getPollId().toString(), "Wie geht es Ihnen heute?", Arrays.asList("Gut", "In Ordnung", "Schlecht"), "ChoiceQuestion");
        Question q4 = questionService.addQuestion(testPoll.getPollId().toString(), "Was hat Sie am Meisten überzeugt?", Arrays.asList("Die Vorträge", "Die Informationsstände", "Das Catering", "Ich kann mich nicht entscheiden"), "ChoiceQuestion");
        Question q5 = questionService.addQuestion(testPoll.getPollId().toString(), "Werden Sie uns nächstes Jahr wieder besuchen?", Arrays.asList("Ja", "Nein", "Vielleicht"), "ChoiceQuestion");
        Question q6 = questionService.addQuestion(testPoll.getPollId().toString(), "Wie viel Zeit haben sie auf der Messe verbracht?", Arrays.asList("unter einer Stunde", "1-2 Stunden", "2-5 Stunden", "über 5 Stunden"), "ChoiceQuestion");
        for (int i = 0; i < 10; i++) {
            answerService.giveAnswer(tbettmannUserName, testPoll.getPollId().toString(), q1.getQuestionId().toString(), Collections.singletonList("0"));
        } */
    }
}
