package gpse.umfrato.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gpse.umfrato.domain.Evaluation.DiagramData;
import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.answer.AnswerService;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.cmd.AnswerCmd;
import gpse.umfrato.domain.cmd.FilterCmd;
import gpse.umfrato.domain.cmd.PollCmd;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.pollresult.PollResultServiceImpl;
import gpse.umfrato.domain.question.Question;
import gpse.umfrato.domain.question.QuestionService;
import gpse.umfrato.domain.user.User;
import gpse.umfrato.domain.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
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
    private ObjectMapper objectMapper;

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
        testAnswers();
/*        LOGGER.info("PollId");
        LOGGER.info(pollCmd.getId());
        try {
            pollService.getPoll(pollCmd.getId());
        } catch (Exception e)
        {
            LOGGER.info(e.getLocalizedMessage());
        }*/
        String cheetSheet = "{\"id\": 1,\"title\": \"Wie hat" +
            " Ihnen die Veranstaltung insgesamt gefallen?\",\"answerPossibilities\": [\"Sehr gut\",\"Gut\",\"Überwieg" +
            "end gut\",\"Schlecht\",\"Ich weiß nicht\"],\"data\": [70,65,30,5,25]},{\"id\": 2,\"title\": \"Welches Ge" +
            "schlecht haben Sie?\",\"answerPossibilities\": [\"Weiblich\",\"Männlich\",\"Divers\"],\"data\": [20,19,1" +
            "]},{\"id\": 3,\"title\": \"Wie geht es Ihnen heute?\",\"answerPossibilities\": [\"Gut\",\"In Ordnung\"," +
            "\"Schlecht\"],\"data\": [22,8,7]},{\"id\": 4,\"title\": \"Was hat Sie am Meisten überzeugt?\",\"answerPo" +
            "ssibilities\": [\"Die Vorträge\",\"Die Informationsstände\",\"Das Catering\",\"Ich kann mich nicht entsc" +
            "heiden\"],\"data\": [17,8,4,2]},{\"id\": 5,\"title\": \"Werden Sie uns nächstes Jahr wieder besuchen?\"," +
            "\"answerPossibilities\": [\"Ja\",\"Nein\"],\"data\": [50,21]},{\"id\": 6,\"title\": \"Wie viel Zeit habe" +
            "n sie auf der Messe verbracht?\",\"answerPossibilities\": [\"unter einer Stunde\",\"1-2 Stunden\",\"2-5 " +
            "Stunden\",\"über 5 Stunden\"],\"data\": [12,45,40,20]}";
        String data = "{\"name\":\"" + pollService.getPoll("1").getPollName() + "\",\"questionList\": [$]}";
        cheetSheet = data.replace("$",cheetSheet);
        List<Question> questions = questionService.getAllQuestions(1L);
        while(questions.size() > 6)
        {
            questions.remove(6);
        }
        for(Question q:questions)
        {
            if(q.getQuestionType().equals("ChoiceQuestion"))
            {
                List<Answer> answers = answerService.getAnswerFromOneQuestion(q.getQuestionId());
                List<Integer> answerData = new ArrayList<>();
                for (int i = 0; i < q.getAnswerPossibilities().size(); i++) {
                    answerData.add(0);
                }
                for (Answer a: answers) {
                    for (String s: a.getGivenAnswerList()) {
                        Integer index = Integer.valueOf(s);
                        answerData.set(index,answerData.get(index) + 1);
                    }
                }
                DiagramData dd = new DiagramData(q.getQuestionId(), q.getQuestionMessage(), q.getAnswerPossibilities(), answerData);
                data = data.replace("$", dd.toJSON() + ",$");
            }
        }
        data = data.replace(",$","");
        System.out.println("vorlage");
        System.out.println(cheetSheet);
        System.out.println("sende");
        System.out.println(data);
        return data;
    }

    /**Returns JSON string to be interpreted, hier wird der jsonInput in ein Filterobjekt deserialisiert und dort können wir herausfinden, um welchen Filter es sich genau handelt.*/
    @PostMapping("/generateDiagram")
    public void populateDiagram(final @RequestBody List<FilterCmd> input) {
        System.out.println(input.toString());
        /*TODO:
            1. Filter parsen
            2. Filter ordnen
            3. Daten auswählen
            4. Rechnen und evtl. wieder zu 3
            5. als Diagramm aufarbeiten
            6. abschicken*/
    }

    @GetMapping("/getDiagram")
    public String sendDiagram(final @RequestBody List<FilterCmd> input) {
            /*TODO:
                1. Filter parsen
                2. Filter ordnen
                3. Daten auswählen
                4. Rechnen und evtl. wieder zu 3
                5. als Diagramm aufarbeiten
                6. abschicken*/
        return "Haha";
    }

    public <T> List<Answer> filterByAnswer(List<Answer> input, List<T> wantedAnswers){
        ListIterator<Answer> iter = input.listIterator();
        List<Answer> output = new ArrayList<>();
        while(iter.hasNext()){
            Answer index = iter.next();
            if(!Collections.disjoint(Arrays.asList(index.getGivenAnswerList()), wantedAnswers))
                output.add(index);
        }
        return output;
    }

    public boolean testAnswers(){
        List<Answer> antworten = answerService.getAnswerFromOneQuestion(1L);
        List<String> wantedAnswers = new ArrayList<>();
        wantedAnswers.add("10");
        System.out.println(filterByAnswer(antworten, wantedAnswers));
        return false;
    }

    public <T> List<Answer> filterByUser(String pollID, String username){ /**TODO: PollResultServiceImpl NEEDS POLLRESULTS ELSE FILTERING BY USER WONT WORK*/
        PollResultServiceImpl pollResultService = new PollResultServiceImpl();
        List<Question> questionList = questionService.getAllQuestions(Long.parseLong(pollID));
        ListIterator<Question> iter = questionList.listIterator();
        List<Answer> output = new ArrayList<>();

        while(iter.hasNext()){
            Question index = iter.next();
            List<Answer> answerListForQuestion = answerService.getAnswerFromOneQuestion(index.getQuestionId());
            ListIterator<Answer> answerIter = answerListForQuestion.listIterator();
            while(answerIter.hasNext()){
                Answer answerIndex = answerIter.next();
                //if(answerIndex.)
            }
        }

        return null;
    }


}
