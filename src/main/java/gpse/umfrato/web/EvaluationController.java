package gpse.umfrato.web;

import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.answer.AnswerService;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.cmd.AnswerCmd;
import gpse.umfrato.domain.cmd.FilterCmd;
import gpse.umfrato.domain.cmd.PollCmd;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.question.QuestionService;
import gpse.umfrato.domain.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Filter;
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
    private final CategoryService categoryService;

    @Autowired
    public EvaluationController(final AnswerService answerService, final UserService userService, final QuestionService questionService, final PollService pollService, final CategoryService categoryService) {
        this.answerService = answerService;
        this.userService = userService;
        this.questionService = questionService;
        this.pollService = pollService;
        this.categoryService = categoryService;
    }

    @GetMapping("/initialDiagrams")
    public String initialDiagrams() {
/*        LOGGER.info("PollId");
        LOGGER.info(pollCmd.getId());
        try {
            pollService.getPoll(pollCmd.getId());
        } catch (Exception e)
        {
            LOGGER.info(e.getLocalizedMessage());
        }*/
        String mockData = "{\"name\":\"Umfrage zur IT-Messe 2020\",\"questionList\": [{\"id\": 1,\"title\": \"Wie hat" +
            " Ihnen die Veranstaltung insgesamt gefallen?\",\"answerPossibilities\": [\"Sehr gut\",\"Gut\",\"Überwieg" +
            "end gut\",\"Schlecht\",\"Ich weiß nicht\"],\"data\": [70,65,30,5,25]},{\"id\": 2,\"title\": \"Welches Ge" +
            "schlecht haben Sie?\",\"answerPossibilities\": [\"Weiblich\",\"Männlich\",\"Divers\"],\"data\": [20,19,1" +
            "]},{\"id\": 3,\"title\": \"Wie geht es Ihnen heute?\",\"answerPossibilities\": [\"Gut\",\"In Ordnung\"," +
            "\"Schlecht\"],\"data\": [22,8,7]},{\"id\": 4,\"title\": \"Was hat Sie am Meisten überzeugt?\",\"answerPo" +
            "ssibilities\": [\"Die Vorträge\",\"Die Informationsstände\",\"Das Catering\",\"Ich kann mich nicht entsc" +
            "heiden\"],\"data\": [17,8,4,2]},{\"id\": 5,\"title\": \"Werden Sie uns nächstes Jahr wieder besuchen?\"," +
            "\"answerPossibilities\": [\"Ja\",\"Nein\"],\"data\": [50,21]},{\"id\": 6,\"title\": \"Wie viel Zeit habe" +
            "n sie auf der Messe verbracht?\",\"answerPossibilities\": [\"unter einer Stunde\",\"1-2 Stunden\",\"2-5 " +
            "Stunden\",\"über 5 Stunden\"],\"data\": [12,45,40,20]}]}";
        return mockData;
    }

    /**Returns JSON string to be interpreted, hier wird der jsonInput in ein Filterobjekt deserialisiert und dort können wir herausfinden, um welchen Filter es sich genau handelt.*/
    @GetMapping("/generateDiagram")
    public String populateDiagram(final @RequestBody List<FilterCmd> input) {
        /*TODO:
            1. Filter parsen
            2. Filter ordnen
            3. Daten auswählen
            4. Rechnen und evtl. wieder zu 3
            5. als Diagramm aufarbeiten
            6. abschicken*/
        return null;
    }



}
