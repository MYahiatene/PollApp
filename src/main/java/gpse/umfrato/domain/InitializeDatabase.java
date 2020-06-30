package gpse.umfrato.domain;

import gpse.umfrato.domain.answer.AnswerService;
import gpse.umfrato.domain.mail.CsvReader;
import gpse.umfrato.domain.mail.MailService;
import gpse.umfrato.domain.participationlinks.ParticipationLinkService;
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
import java.util.List;

@Service
public class InitializeDatabase implements InitializingBean {

    /**
     * The question service.
     */
    /* default */ final QuestionService questionService;

    /**
     * The answer service.
     */
    /* default */ final AnswerService answerService;

    /**
     * The poll service.
     */
    /* default */ final PollService pollService;

    /**
     * The ParticipationLink service.
     */
    /* default */ final ParticipationLinkService participationLinkService;

    /**
     * The category service.
     */
    /* default */ final CategoryService categoryService;

    /**
     * The user service.
     */
    /* default */ private final UserService userService;

    /* default */ private final CsvReader csvReader = new CsvReader();

    /* default */ private final MailService mailService;

    /**
     * This method initializes the database.
     *
     * @param userService              the object user service
     * @param pollService              the object poll service
     * @param questionService          the object question service
     * @param answerService            the object answer service
     * @param participationLinkService the object participationLink service
     * @param categoryService          the object category service
     */

    @Autowired
    public InitializeDatabase(final UserService userService, final PollService pollService,
                              final QuestionService questionService, final AnswerService answerService,
                              final ParticipationLinkService participationLinkService,
                              final CategoryService categoryService, final MailService mailService) {

        this.userService = userService;

        this.pollService = pollService;

        this.questionService = questionService;

        this.answerService = answerService;

        this.participationLinkService = participationLinkService;

        this.categoryService = categoryService;

        this.mailService = mailService;

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

        String path = "src/main/java/gpse/umfrato/domain/mail/testMails.csv";
        List<String> maillist = csvReader.readEmailsFromCsvPath(path);
        mailService.setMailAdresses(maillist);
    }
}
