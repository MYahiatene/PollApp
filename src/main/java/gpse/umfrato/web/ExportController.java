package gpse.umfrato.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.pollresult.PollResult;
import gpse.umfrato.domain.pollresult.PollResultService;
import gpse.umfrato.domain.question.Question;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequestMapping(value = "/api/export/", method = RequestMethod.POST)
@RestController
@CrossOrigin
public class ExportController {
    static final Logger LOGGER = Logger.getLogger("ExportController");
    private final PollService pollService;
    private final PollResultService pollResultService;

    public ExportController(PollService pollService, PollResultService pollResultService) {
        this.pollService = pollService;
        this.pollResultService = pollResultService;
    }

    /**New Idea: Convert to JSON first and then to CSV, it sure ain't pretty but if it works it's fine*/

    @RequestMapping(value = "/api/export/", method = RequestMethod.POST)
    @CrossOrigin
    public static String toCSVManual(Poll poll){
    /**Name, PollID, Creator, Anonymität, Kategorie 1, Kategorie 1, Kategorie 2*/
    /**TestPoll, 1, Tbettmann, 1, Frage 1, Frage 2, Frage 1 aus Kat. 2*/
        String output = "";
        output += "Name,PollID,PollCreator,Anonymitätsstatus";
        int amountOfArgumentsBeforeCategories = 4;
        for(Category category : poll.getCategoryList())
            output += ',' + category.getCategoryName() + ',' + "Antwortmöglichkeiten";
        output += '\n';
        output += poll.getPollName() + ',' + poll.getPollId() + ',' + poll.getPollCreator() + ',' + poll.getAnonymityStatus();
        for(Category category : poll.getCategoryList()) {
            for (Question question : category.getQuestionList()) {
                output += ',' + escapeSpecialCharacters(question.getQuestionMessage()) + ',';
                LOGGER.info(question.getQuestionType());
                for(String possibility : question.getAnswerPossibilities())
                    output +=' ' + possibility;
                if(question.getQuestionType() == "RangeQuestion")
                    output += question.getStartValue() + ".." + question.getEndValue() + " in Inkrementen von " + question.getStepSize();
                if(question.getQuestionType() == "SliderQuestion")
                    output += question.getStartValue() + ".." + question.getEndValue() + " in Inkrementen von " + question.getStepSize();
                output += '\n';
                for(int i = 0; i<amountOfArgumentsBeforeCategories-1; i++) /**Needs to be -1 because of output + escapeSpecial... that comma can't go away*/
                    output += ',';
            }
        }
        output += '\n';
        return output;
    }

    @RequestMapping(value = "/api/export/", method = RequestMethod.POST)
    @CrossOrigin
    public static String toJSON(Poll result) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();
        try {
            return objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new Exception("Serialisierung fehlgeschlagen");
        }
    }

    @RequestMapping(value = "/api/export/", method = RequestMethod.POST)
    @CrossOrigin
    public static Poll fromJSONToPoll(String json) throws Exception {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Poll poll = objectMapper.readValue(json, Poll.class);
            System.out.println(poll.getPollId());
            System.out.println(poll.getPollName());
            return poll;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/api/export/", method = RequestMethod.POST)
    @CrossOrigin
    public static String toJSON(PollResult result) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();
        try {
            return objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new Exception("Serialisierung fehlgeschlagen");
        }
    }

    @RequestMapping(value = "/api/export/", method = RequestMethod.POST)
    @CrossOrigin
    public static PollResult fromJSONToResult(String json) throws Exception {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PollResult pResult = objectMapper.readValue(json, PollResult.class);
            return pResult;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/api/export/", method = RequestMethod.POST)
    @CrossOrigin
    public static String createExportJSON(Poll poll, List<PollResult> result){
        JSONObject combined = new JSONObject();
        try {
            JSONObject pollJSON = new JSONObject();
            pollJSON.getJSONObject(toJSON(poll));
            combined.put("Poll", pollJSON);
            JSONObject resultJSON;
            for(PollResult resultIndex : result) {
                resultJSON = new JSONObject();
                resultJSON.getJSONObject(toJSON(resultIndex));
                combined.put("Results", resultJSON);
            }
            return combined.toString();
        } catch(Exception e){}
        return null;
    }

    private String FormatCSVField(String data)
    {
        return String.format("\"{0}\"",
            data.replace("\"", "\"\"\"")
                .replace("\n", "")
                .replace("\r", "")
        );
    }

    public String addHeaders(Long pollId){
        Poll pollToConvert = pollService.getPoll(pollId);
        List<Category> categories = pollToConvert.getCategoryList();
        ListIterator<Category> categoryIterator = categories.listIterator();

        String columnNamesList = pollToConvert.getPollName(); /**Table headers*/
        /**Structure: PollID, PollName, AnonymityStatus, PollCreator*/
        columnNamesList += pollToConvert.getPollId() + ',' + pollToConvert.getPollName() + ',' + pollToConvert.getAnonymityStatus() + ',' + pollToConvert.getPollCreator() + ',';
        while(categoryIterator.hasNext()) {
            Category singularCategory = categoryIterator.next(); //Page
            ListIterator<Question> questionIterator = singularCategory.getQuestionList().listIterator();
            while(questionIterator.hasNext()) {
                Question singularQuestion = questionIterator.next();
                String singularQuestionMessage = singularQuestion.getQuestionMessage();
                /**TODO: Maybe Object to String will work, I don't know*/
                columnNamesList += "," + singularQuestionMessage; /**I think it works that way, should give out "PollID, Frage1, Frage2, Frage3, ... regardless of category"*/
            }
        }
        return columnNamesList;
    }

    /**So how does it work?: */
    /**Function iterates over the list of pollResults first filling the first row of Elements with the Questions and then filling the rest with the actual PollResults.*/
    /**TODO: Implement way to know what kind of question this was*/

    @PostMapping("/answers/{pollId:\\d+}")
    public String convertPollWithResultsToCSV(final @PathVariable Long pollId) {
        Poll pollToConvert = pollService.getPoll(pollId);
        List<Category> categories = pollToConvert.getCategoryList();
        ListIterator<Category> categoryIterator = categories.listIterator();
        String columnNamesList = addHeaders(pollId);
        /**This is what does the work I guess*/

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File("PollData.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();

        /**IMPORTANT: Structure: PollID; PollTaker; PollResultID; LastEditAt; List of Answers*/
        // No need give the headers Like: id, Name on builder.append
        builder.append(columnNamesList +"\n");
        List<PollResult> results = pollResultService.getPollResults(pollId); /**TODO: WICHTIG KEINE LISTE VON POLLRESULTS*/
        ListIterator<PollResult> resultIterator = results.listIterator();
        while(resultIterator.hasNext()) {
            PollResult singularResult = resultIterator.next();
            ListIterator<Answer> answerIterator = singularResult.getAnswerList().listIterator();
            builder.append(singularResult.getPollId() + ',' + singularResult.getPollTaker() + ',' + singularResult.getPollResultId() + ',' + singularResult.getLastEditAt());
            while(answerIterator.hasNext()) {
                Answer singularAnswer = answerIterator.next();
                /**Iterate over list to make every Answer one column in the csv table*/
                builder.append(answerToCSV(singularAnswer) + ',');
            }
            builder.append('\n');
        }
        pw.write(builder.toString());
        pw.close();
        LOGGER.info("done!");

        return builder.toString();
    }

    public String convertReadableQuestionsToCSV(final @PathVariable Long pollId) {
        Poll pollToConvert = pollService.getPoll(pollId);
        List<Category> categories = pollToConvert.getCategoryList();
        ListIterator<Category> categoryIterator = categories.listIterator();

        String columnNamesList = pollToConvert.getPollName(); /**Table headers*/
        while(categoryIterator.hasNext()) {
            Category singularCategory = categoryIterator.next(); //Page
            ListIterator<Question> questionIterator = singularCategory.getQuestionList().listIterator();
            while(questionIterator.hasNext()) {
                Question singularQuestion = questionIterator.next();
                String singularQuestionMessage = singularQuestion.getQuestionMessage();
                columnNamesList += '\n' + singularQuestionMessage; /**I think it works that way, should give out "PollID, Frage1, Frage2, Frage3, ... regardless of category"*/
            }
        }

        /**This is what does the work I guess*/

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File("PollData.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();


        // No need give the headers Like: id, Name on builder.append
        builder.append(columnNamesList +"\n");
        pw.write(builder.toString());
        pw.close();
        LOGGER.info("done!");

        return builder.toString();
    }

    public String answerToCSV(Answer input) {
        ListIterator<String> answerIterator = input.getGivenAnswerList().listIterator();
        String output = "";
        while(answerIterator.hasNext()) {
            String answerForSingularQuestion = answerIterator.next();
            output += answerForSingularQuestion + ",";
        }
        output += '\n';
        return output;
    }

    public static String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

}
