package gpse.umfrato.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import gpse.umfrato.domain.evaluation.Statistics;
import gpse.umfrato.domain.answer.AnswerService;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.cmd.FilterCmd;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.pollresult.PollResultService;
import gpse.umfrato.domain.question.QuestionService;
import gpse.umfrato.domain.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.logging.Logger;

@RequestMapping(value = "/api/evaluation", method = RequestMethod.GET)
@RestController
@CrossOrigin
public class EvaluationController {

    private static final Logger LOGGER = Logger.getLogger("EvaluationController");
    private final AnswerService answerService;
    private final UserService userService;
    private final QuestionService questionService;
    private final PollService pollService;
    private final PollResultService pollResultService;
    private final CategoryService categoryService;
    enum Filter {
        ANSWER_FILTER, USER_FILTER
    }
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    public EvaluationController(final AnswerService answerService, final UserService userService,
                                final QuestionService questionService, final PollService pollService,
                                final PollResultService pollResultService,  final CategoryService categoryService) {
        this.answerService = answerService;
        this.userService = userService;
        this.questionService = questionService;
        this.pollService = pollService;
        this.pollResultService = pollResultService;
        this.categoryService = categoryService;
    }

    /**
     * generates Data to display in Diagrams pased of of filters provided in input.
     * @param input first filter must be of type DataFilter containing the Poll to analyse
     * @return a string in json format with all the data to display
     */
    @PostMapping("/generateDiagram")
    public String populateDiagram(final @RequestBody List<FilterCmd> input) {
        LOGGER.info(input.toString());
        if (input.isEmpty()) {
            return "?";
        }
        final Statistics calculation = new Statistics(answerService, userService, questionService, pollService,
                pollResultService, categoryService, input.get(0));
        calculation.loadFilter(input);
        return calculation.generateDiagram();
    }
}
