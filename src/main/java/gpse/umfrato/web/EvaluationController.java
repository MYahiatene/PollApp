package gpse.umfrato.web;

import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.answer.AnswerService;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.cmd.AnswerCmd;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.question.QuestionService;
import gpse.umfrato.domain.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Filter;

@RequestMapping(value = "/api/evaluation", method = RequestMethod.GET)
@RestController
@CrossOrigin
public class EvaluationController {

    private final AnswerService answerService;
    private final UserService userService;
    private final QuestionService questionService;
    private final PollService pollService;
    private final CategoryService categoryService;

    @Autowired
    public EvaluationController(final AnswerService answerService, final UserService userService, final QuestionService questionService, final PollService pollService, final CategoryService categoryService) {
        this.answerService = answerService;
        this.userService = userService;
        this.questionService = questionService;
        this.pollService = pollService;
        this.categoryService = categoryService;
    }

    /**Returns JSON string to be interpreted, hier wird der jsonInput in ein Filterobjekt deserialisiert und dort können wir herausfinden, um welchen Filter es sich genau handelt.*/
    /*TODO: Filter erstellen, Daten auswählen*/
    @GetMapping("/generateDiagram")
    public String populateDiagram(final @RequestBody String jsonInput) {



        return null;
    }



}
