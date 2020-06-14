package gpse.umfrato.domain;

import gpse.umfrato.domain.category.CategoryService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        final String logoUrl = "https://picsum.photos/510/300?random";
        final Poll testPoll = new Poll(tbettmannUserName, "anonym", "Umfrage IT-Messe 2020",
            Instant.now().toString(),
            Instant.now().toString(), Instant.now().toString(), 0, "#FF9600", "#00FF00", logoUrl, true, true);

        try {
            userService.loadUserByUsername(tbettmannUserName);
            pollService.createPoll(testPoll);

            questionService.addQuestion("1", "testFrage", Arrays.asList("Ja", "Nein", "Vielleicht"), "ChoiceQuestion", 0, 0, 0, null, null, false, 1, false, 10, 100);
            questionService.addQuestion("1", "testFrage2", Arrays.asList("Jein", "Fein", "Vielschwer"), "ChoiceQuestion", 0, 0, 0, null, null, false, 2, false, 10, 100);
            questionService.addQuestion("1", "testFrage3", new ArrayList<>(), "TextQuestion", 0, 0, 0, null, null, false, 3, false, 10, 100);
            questionService.addQuestion("1", "TestFrage 4", new ArrayList<>(), "RangeQuestion", 100, 10, 10, "belowMessage", "aboveMessage", false, 4, false, 10, 100);
            questionService.addQuestion("1", "TestFrage 5", new ArrayList<>(), "SliderQuestion", 100, 0, 10, "small", "big", false, 5, false, 10, 100);
            questionService.addQuestion("1", "TestFrage 5", new ArrayList<>(), "SliderQuestion", 100, 0, 10, "small", "big", false, 6, false, 10, 100);
            questionService.addQuestion("1", "testFrage 6", Arrays.asList("Ja", "Nein", "Vielleicht"), "RadioButton", 0, 0, 0, null, null, false, 7, false, 10, 100);

            userService.loadUserByUsername(tbettmannUserName);
        } catch (UsernameNotFoundException e) {
            userService.createUser(tbettmannUserName, dummyPassword, "Tobias", "Bettmann",
                "Admin", "tbettmann@reply.de");
        }

        try {
            userService.loadUserByUsername("testnutzer");
        } catch (UsernameNotFoundException ex) {
            userService.createUser("testNutzer", dummyPassword, "Markus", "Mueller",
                "Teilnehmer", "mmueller@gmx.de");
        }
        pollService.createPoll(testPoll);

        final Question q8 = new Question();
        q8.setQuestionType("RangeQuestion");
        q8.setQuestionMessage("Wie alt sind Sie?");
        q8.setBelowMessage("Jünger");
        q8.setStartValue(20f);
        q8.setStepSize(10f);
        q8.setEndValue(70);
        q8.setAboveMessage("Älter");
        final List<String> answers = Arrays.asList("Ich fand die Messe scheiße", "Die Messe war super",
            "Eigentlich relativ ok", "Was weiß ich denn bitte?", "Stände waren gut, Catering nicht");
    }
}
