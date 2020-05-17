package gpse.umfrato.domain;

import gpse.umfrato.domain.answer.AnswerService;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.question.QuestionService;
import gpse.umfrato.domain.user.UserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Arrays;

@Service
public class InitializeDatabase implements InitializingBean {

    private final UserService userService;

    private final PollService pollService;

    private final QuestionService questionService;

    private final AnswerService answerService;

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

        final String testUser = "testUser";
        final String one = "1";

        final Poll testPoll = new Poll(testUser, "anonym", "testPoll", Instant.now().toString(),
            Instant.now().toString(), Instant.now().toString(), 0);

        try {
            userService.createUser(testUser, "hallo", "test", "heay",
                "Admin", "User");
            pollService.createPoll(testPoll);
            questionService.addQuestion(one, "testFrage", Arrays.asList("Frage1", "Frage2", "Frage3"), "freitext");
            answerService.giveAnswer(testUser, one, "3", Arrays.asList("Ja", "Nein"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
