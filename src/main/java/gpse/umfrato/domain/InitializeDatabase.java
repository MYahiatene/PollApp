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
import java.util.Arrays;
import java.util.Collections;

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

        final String tbettmannUserName = "tbettmann";
        final String dummyPassword = "{bcrypt}$2a$10$WoG5Z4YN9Z37EWyNCkltyeFr6PtrSXSLMeFWOeDUwcanht5CIJgPa";


        final Poll testPoll = new Poll(tbettmannUserName, "anonym", "testPoll", Instant.now().toString(),
            Instant.now().toString(), Instant.now().toString(), 0);

        try {
            userService.loadUserByUsername(tbettmannUserName);
            pollService.createPoll(testPoll);

            //questionService.addQuestion(one, "testFrage", Arrays.asList("Frage1", "Frage2", "Frage3"), "freitext");
            //answerService.giveAnswer(testUsername, one, "3", Arrays.asList("Ja", "Nein"));
        } catch (UsernameNotFoundException e) {
            userService.createUser(tbettmannUserName, dummyPassword, "Tobias", "Bettmann",
                "Admin","tbettmann@reply.de");
        }

        try {
            userService.loadUserByUsername("testnutzer");
        } catch (UsernameNotFoundException ex) {
            userService.createUser("testNutzer", dummyPassword, "Markus", "Mueller",
                "Teilnehmer","mmueller@gmx.de");
        }
        pollService.createPoll(testPoll);
        Question q1 = questionService.addQuestion(testPoll.getPollId().toString(),"Wie hat Ihnen die Veranstaltung insgesamt gefallen?",Arrays.asList("Sehr gut", "Gut", "Überwiegend gut", "Schlecht", "Ich weiß nicht"), "ChoiceQuestion");
        Question q2 = questionService.addQuestion(testPoll.getPollId().toString(),"Welches Geschlecht haben Sie?",Arrays.asList("Weiblich", "Männlich", "Divers"), "ChoiceQuestion");
        Question q3 = questionService.addQuestion(testPoll.getPollId().toString(),"Wie geht es Ihnen heute?",Arrays.asList("Gut", "In Ordnung", "Schlecht"), "ChoiceQuestion");
        Question q4 = questionService.addQuestion(testPoll.getPollId().toString(),"Was hat Sie am Meisten überzeugt?",Arrays.asList("Die Vorträge", "Die Informationsstände", "Das Catering", "Ich kann mich nicht entscheiden"), "ChoiceQuestion");
        Question q5 = questionService.addQuestion(testPoll.getPollId().toString(),"Werden Sie uns nächstes Jahr wieder besuchen?",Arrays.asList("Ja", "Nein", "Vielleicht"), "ChoiceQuestion");
        Question q6 = questionService.addQuestion(testPoll.getPollId().toString(),"Wie viel Zeit haben sie auf der Messe verbracht?",Arrays.asList("unter einer Stunde", "1-2 Stunden", "2-5 Stunden", "über 5 Stunden"), "ChoiceQuestion");
        for (int i = 0; i < 70; i++) {
            answerService.giveAnswer(tbettmannUserName,testPoll.getPollId().toString(),q1.getQuestionId().toString(), Collections.singletonList("0"));
        }
        for (int i = 0; i < 65; i++) {
            answerService.giveAnswer(tbettmannUserName,testPoll.getPollId().toString(),q1.getQuestionId().toString(), Collections.singletonList("1"));
        }
        for (int i = 0; i < 30; i++) {
            answerService.giveAnswer(tbettmannUserName,testPoll.getPollId().toString(),q1.getQuestionId().toString(), Collections.singletonList("2"));
        }
        for (int i = 0; i < 5; i++) {
            answerService.giveAnswer(tbettmannUserName,testPoll.getPollId().toString(),q1.getQuestionId().toString(), Collections.singletonList("3"));
        }
        for (int i = 0; i < 25; i++) {
            answerService.giveAnswer(tbettmannUserName,testPoll.getPollId().toString(),q1.getQuestionId().toString(), Collections.singletonList("4"));
        }
        for (int i = 0; i < 20; i++) {
            answerService.giveAnswer(tbettmannUserName,testPoll.getPollId().toString(),q2.getQuestionId().toString(), Collections.singletonList("0"));
        }
        for (int i = 0; i < 19; i++) {
            answerService.giveAnswer(tbettmannUserName,testPoll.getPollId().toString(),q2.getQuestionId().toString(), Collections.singletonList("1"));
        }
        for (int i = 0; i < 1; i++) {
            answerService.giveAnswer(tbettmannUserName,testPoll.getPollId().toString(),q2.getQuestionId().toString(), Collections.singletonList("2"));
        }
        for (int i = 0; i < 22; i++) {
            answerService.giveAnswer(tbettmannUserName,testPoll.getPollId().toString(),q3.getQuestionId().toString(), Collections.singletonList("0"));
        }
        for (int i = 0; i < 8; i++) {
            answerService.giveAnswer(tbettmannUserName,testPoll.getPollId().toString(),q3.getQuestionId().toString(), Collections.singletonList("1"));
        }
        for (int i = 0; i < 7; i++) {
            answerService.giveAnswer(tbettmannUserName,testPoll.getPollId().toString(),q3.getQuestionId().toString(), Collections.singletonList("2"));
        }
        for (int i = 0; i < 17; i++) {
            answerService.giveAnswer(tbettmannUserName,testPoll.getPollId().toString(),q4.getQuestionId().toString(), Collections.singletonList("0"));
        }
        for (int i = 0; i < 8; i++) {
            answerService.giveAnswer(tbettmannUserName,testPoll.getPollId().toString(),q4.getQuestionId().toString(), Collections.singletonList("1"));
        }
        for (int i = 0; i < 4; i++) {
            answerService.giveAnswer(tbettmannUserName,testPoll.getPollId().toString(),q4.getQuestionId().toString(), Collections.singletonList("2"));
        }
        for (int i = 0; i < 2; i++) {
            answerService.giveAnswer(tbettmannUserName,testPoll.getPollId().toString(),q4.getQuestionId().toString(), Collections.singletonList("3"));
        }
        for (int i = 0; i < 50; i++) {
            answerService.giveAnswer(tbettmannUserName,testPoll.getPollId().toString(),q5.getQuestionId().toString(), Collections.singletonList("0"));
        }
        for (int i = 0; i < 21; i++) {
            answerService.giveAnswer(tbettmannUserName,testPoll.getPollId().toString(),q5.getQuestionId().toString(), Collections.singletonList("1"));
        }
        for (int i = 0; i < 12; i++) {
            answerService.giveAnswer(tbettmannUserName,testPoll.getPollId().toString(),q6.getQuestionId().toString(), Collections.singletonList("0"));
        }
        for (int i = 0; i < 45; i++) {
            answerService.giveAnswer(tbettmannUserName,testPoll.getPollId().toString(),q6.getQuestionId().toString(), Collections.singletonList("1"));
        }
        for (int i = 0; i < 40; i++) {
            answerService.giveAnswer(tbettmannUserName,testPoll.getPollId().toString(),q6.getQuestionId().toString(), Collections.singletonList("2"));
        }
        for (int i = 0; i < 20; i++) {
            answerService.giveAnswer(tbettmannUserName,testPoll.getPollId().toString(),q6.getQuestionId().toString(), Collections.singletonList("3"));
        }
    }
}
