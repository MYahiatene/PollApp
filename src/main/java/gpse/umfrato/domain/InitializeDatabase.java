package gpse.umfrato.domain;

import gpse.umfrato.domain.answer.AnswerService;
import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.participationlinks.ParticipationLinkService;
import gpse.umfrato.domain.category.CategoryService;
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
import java.net.MalformedURLException;
import java.util.Arrays;

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
        } catch (UsernameNotFoundException e) {
            userService.createUser(tbettmannUserName, dummyPassword, "Tobias", "Bettmann",
                "Admin", "tbettmann@reply.de");
        }
        Poll p = new Poll(tbettmannUserName,"1","Umfrage","16.06.2020","18.06.2020","20.06.2020",0,"#fcba03","#fc6203","",true,true);
        p = pollService.createPoll(p);
        participationLinkService.createParticipationLink(p.getPollId(), "allUsers");
        Category c1 = categoryService.getAllCategories(p.getPollId()).get(0);
        Category c2 = categoryService.createCategory("Kategorie 2",p.getPollId());
        Question q1 = questionService.addQuestion(p.getPollId(),new Question("Radiobuttons", Arrays.asList("1","2","3","4"), 1, true));
        Question q2 = questionService.addQuestion(p.getPollId(),new Question("Checkboxen", Arrays.asList("1","2","3","4"), 3, true));
        Question q3 = questionService.addQuestion(p.getPollId(),new Question("Textfrage Single Line", true, 1, 50));
        Question q4 = questionService.addQuestion(p.getPollId(),new Question("Textfrage Multiline", false, 0, 150));
        Question q5 = questionService.addQuestion(p.getPollId(),new Question("Rangequestion", 20.0f, 80.0f, 10.0f, "Unter 20", "Über 80"));
        Question q6 = questionService.addQuestion(p.getPollId(),new Question("Sliderfrage nach Gefühl", 0, 1, 0.01f, "Wenig", "Viel", true));
        Question q7 = questionService.addQuestion(p.getPollId(),new Question("Sliderfrage nach Wert", 0, 5000, 1, "Unten", "Oben", false));
        questionService.changeCategory(q5.getQuestionId(),c1.getCategoryId(),c2.getCategoryId());
        questionService.changeCategory(q6.getQuestionId(),c1.getCategoryId(),c2.getCategoryId());
        questionService.changeCategory(q7.getQuestionId(),c1.getCategoryId(),c2.getCategoryId());
        System.out.println(pollService.getPoll(p.getPollId()).toString());
    }
}
