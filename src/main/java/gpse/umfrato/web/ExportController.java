package gpse.umfrato.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.pollresult.PollResult;
import gpse.umfrato.domain.pollresult.PollResultService;
import gpse.umfrato.domain.question.Question;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;
import java.io.*;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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


    public static String toJSON(Poll result) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();
        try {
            return objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new Exception("Serialisierung fehlgeschlagen");
        }
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

    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    public String convertToCSV(String[] data) {
        return Stream.of(data)
            .map(this::escapeSpecialCharacters)
            .collect(Collectors.joining(","));
    }
}
