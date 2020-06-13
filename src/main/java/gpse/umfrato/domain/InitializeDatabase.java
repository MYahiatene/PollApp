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
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
        final String logoUrl = "https://picsum.photos/510/300?random";
        final Poll testPoll = new Poll(tbettmannUserName, "anonym", "Umfrage IT-Messe 2020",
                Instant.now().toString(),
            Instant.now().toString(), Instant.now().toString(), 0, "#FF9600", "#00FF00", logoUrl, true, true);

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
        final Question q1 = questionService.addQuestion(testPoll.getPollId().toString(),
                "Wie hat Ihnen die Veranstaltung insgesamt gefallen?", Arrays.asList("Sehr gut", "Gut",
                        "Überwiegend gut", "Schlecht", "Ich weiß nicht"), "ChoiceQuestion");
        final Question q2 = questionService.addQuestion(testPoll.getPollId().toString(),
                "Welches Geschlecht haben Sie?", Arrays.asList("Weiblich", "Männlich", "Divers"),
                "ChoiceQuestion");
        final Question q3 = questionService.addQuestion(testPoll.getPollId().toString(),
                "Wie geht es Ihnen heute?",Arrays.asList("Gut", "In Ordnung", "Schlecht"),
                "ChoiceQuestion");
        final Question q4 = questionService.addQuestion(testPoll.getPollId().toString(),
                "Was hat Sie am Meisten überzeugt?",Arrays.asList("Die Vorträge",
                        "Die Informationsstände", "Das Catering", "Ich kann mich nicht entscheiden"),
                "ChoiceQuestion");
        final Question q5 = questionService.addQuestion(testPoll.getPollId().toString(),
                "Werden Sie uns nächstes Jahr wieder besuchen?",Arrays.asList("Ja", "Nein",
                        "Vielleicht"), "ChoiceQuestion");
        final Question q6 = questionService.addQuestion(testPoll.getPollId().toString(),
                "Wie viel Zeit haben sie auf der Messe verbracht?",Arrays.asList("unter einer Stunde",
                        "1-2 Stunden", "2-5 Stunden", "über 5 Stunden"), "ChoiceQuestion");
        final Question q7 = questionService.addQuestion(testPoll.getPollId().toString(),
                "Beschreiben Sie die Messe in ein bis 5 Wörtern", Collections.emptyList(),
                "TextQuestion");
        final List<String> answers = Arrays.asList("Ich fand die Messe scheiße", "Die Messe war super",
                "Eigentlich relativ ok", "Was weiß ich denn bitte?", "Stände waren gut, Catering nicht");
        for (int i = 0; i < 150; i++) {
            answerService.giveAnswer(String.valueOf(i), testPoll.getPollId().toString(), q1.getQuestionId().toString(),
                    Collections.singletonList(String.valueOf(ThreadLocalRandom.current()
                            .nextInt(0, q1.getAnswerPossibilities().size()))));
            answerService.giveAnswer(String.valueOf(i), testPoll.getPollId().toString(), q2.getQuestionId().toString(),
                    Collections.singletonList(String.valueOf(ThreadLocalRandom.current()
                            .nextInt(0, q2.getAnswerPossibilities().size()))));
            answerService.giveAnswer(String.valueOf(i), testPoll.getPollId().toString(), q3.getQuestionId().toString(),
                    Collections.singletonList(String.valueOf(ThreadLocalRandom.current()
                            .nextInt(0, q3.getAnswerPossibilities().size()))));
            answerService.giveAnswer(String.valueOf(i), testPoll.getPollId().toString(), q4.getQuestionId().toString(),
                    Collections.singletonList(String.valueOf(ThreadLocalRandom.current()
                            .nextInt(0, q4.getAnswerPossibilities().size()))));
            answerService.giveAnswer(String.valueOf(i), testPoll.getPollId().toString(), q5.getQuestionId().toString(),
                    Collections.singletonList(String.valueOf(ThreadLocalRandom.current()
                            .nextInt(0, q5.getAnswerPossibilities().size()))));
            answerService.giveAnswer(String.valueOf(i), testPoll.getPollId().toString(), q6.getQuestionId().toString(),
                    Collections.singletonList(String.valueOf(ThreadLocalRandom.current()
                            .nextInt(0, q6.getAnswerPossibilities().size()))));
            answerService.giveAnswer(String.valueOf(i), testPoll.getPollId().toString(), q7.getQuestionId().toString(),
                    Collections.singletonList(answers.get(ThreadLocalRandom.current()
                            .nextInt(0, answers.size() - 1))));
        }
    }
}
