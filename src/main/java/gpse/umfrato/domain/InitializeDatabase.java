package gpse.umfrato.domain;

import gpse.umfrato.domain.answer.AnswerService;
import gpse.umfrato.domain.participationLinks.ParticipationLinkRepository;
import gpse.umfrato.domain.participationLinks.ParticipationLinkService;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.question.QuestionService;
import gpse.umfrato.domain.user.UserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.net.MalformedURLException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

@Service
public class InitializeDatabase implements InitializingBean {

    /* default */ final QuestionService questionService;

    /* default */ final AnswerService answerService;

    private final UserService userService;

    private final PollService pollService;

    private final ParticipationLinkService participationLinkService;

    private final CategoryService categoryService;

    /**
     * This method initializes the database.
     *
     * @param userService              the object user service
     * @param pollService              the object poll service
     * @param questionService          the object question service
     * @param answerService            the object answer service
     * @param participationLinkService the object participationLink service
     */

    @Autowired
    public InitializeDatabase(final UserService userService, final PollService pollService,
                              final QuestionService questionService, final AnswerService answerService, ParticipationLinkService participationLinkService,
                              final CategoryService categoryService) {

        this.userService = userService;

        this.pollService = pollService;

        this.questionService = questionService;

        this.answerService = answerService;

        this.participationLinkService = participationLinkService;

        this.categoryService = categoryService;
    }

    /**
     * This method initializes the database with an user, a poll, a question and an answer.
     */
    @Override
    @Transactional
    public void afterPropertiesSet() throws MalformedURLException {

        final String tbettmannUserName = "tbettmann";
        final String dummyPassword = "{bcrypt}$2a$10$WoG5Z4YN9Z37EWyNCkltyeFr6PtrSXSLMeFWOeDUwcanht5CIJgPa";
        try {
            userService.loadUserByUsername(tbettmannUserName);

            //questionService.addQuestion(one, "testFrage", Arrays.asList("Frage1", "Frage2", "Frage3"), "freitext");
            //answerService.giveAnswer(testUsername, one, "3", Arrays.asList("Ja", "Nein"));
        } catch (UsernameNotFoundException e) {
            userService.createUser(tbettmannUserName, dummyPassword, "Tobias", "Bettmann",
                "Admin", "tbettmann@reply.de");
        }

        //participationLinkService.createParticipationLink(testPoll.getPollId(), tbettmannUserName);

        try {
            userService.loadUserByUsername("testnutzer");
        } catch (UsernameNotFoundException ex) {
            userService.createUser("testNutzer", dummyPassword, "Markus", "Mueller",
                "Teilnehmer", "mmueller@gmx.de");
        }
    }
}
