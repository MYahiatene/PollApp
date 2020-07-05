package gpse.umfrato.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.cmd.FilterCmd;
import gpse.umfrato.domain.evaluation.Statistics;
import gpse.umfrato.domain.export.ExportService;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.pollresult.PollResult;
import gpse.umfrato.domain.pollresult.PollResultService;
import gpse.umfrato.domain.question.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.io.FileSystemResource;
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

@RequestMapping(value = "/api/export/")
@RestController
@CrossOrigin
public class ExportController {
    static final Logger LOGGER = Logger.getLogger("ExportController");
    public static final String FILES = "src/main/java/gpse/umfrato/domain/export/files/";
    private final PollService pollService;
    private final PollResultService pollResultService;
    private final ExportService exportService;

    @Autowired
    public ExportController(PollService pollService, PollResultService pollResultService, ExportService exportService) {
        this.pollService = pollService;
        this.pollResultService = pollResultService;
        this.exportService = exportService;
    }

    @RequestMapping(value = "/getFile/{pollId:\\d+}", method = RequestMethod.GET) // This should be a string, I don't know
    @ResponseBody
    public FileSystemResource getFile(@PathVariable Long pollId) {
        return new FileSystemResource(new File(FILES +pollId.toString()+".txt"));
    }

    @RequestMapping(value = "/getResult/{pollId:\\d+}", method = RequestMethod.GET) // This should be a string, I don't know
    @ResponseBody
    public FileSystemResource getResult(@PathVariable Long pollId) {
        return new FileSystemResource(new File(FILES+"Results"+pollId.toString()+".txt"));
    }

    @RequestMapping(value = "/getCSV/{pollId:\\d+}", method = RequestMethod.GET) // This should be a string, I don't know
    @ResponseBody
    public FileSystemResource getCSV(@PathVariable Long pollId) {
        return new FileSystemResource(new File(FILES+"CSV"+pollId.toString()+".txt"));
    }

    public void writeToFile(String s, String pollId) throws FileNotFoundException {
        File file = new File(FILES+pollId+".txt");
        PrintWriter out = new PrintWriter(file);
        out.println(s);
        out.close();
        out.flush();
    }

    public void writeToFileCSV(String s, String pollId) throws FileNotFoundException {
        File file = new File(FILES+pollId+".txt");
        PrintWriter out = new PrintWriter(file);
        for(String index : s.split("\n")) {
            out.append(index);
            out.append('\n');
        }
        out.close();
        out.flush();
    }

    @PostMapping("/toCSVPoll/{pollId:\\d+}")
    public String toCSVManual(final @PathVariable Long pollId) throws Exception{
        Poll poll = pollService.getPoll(pollId);
        writeToFileCSV(exportService.toCSVManual(poll), "CSV"+pollId.toString());
        return exportService.toCSVManual(poll);
    }

    @PostMapping("/toJSONPoll/{pollId:\\d+}")
    public String toJSON(final @PathVariable Long pollId) throws Exception {
        Poll result = pollService.getPoll(pollId);
        writeToFile(exportService.toJSON(result), pollId.toString());
        System.out.println(exportService.toJSON(result));
        return exportService.toJSON(result);
    }

    @PostMapping("/importPoll")
    public Poll fromJSONToPoll(final @RequestBody String file) throws Exception {
        System.out.println("In ImportPoll");
        System.out.println(file);
        Poll poll = exportService.fromJSONToPoll(file);
        System.out.println(poll.getPollName());
        poll.setPollId(null);
        pollService.createPoll(poll);
        return poll;
    }

    @PostMapping("/toJSONPollResult/{pollId:\\d+}")
    public String toJSONResult(final @PathVariable Long pollId) throws Exception {
        List<PollResult> results = pollResultService.getPollResults(pollId);
        writeToFile(exportService.toJSON(results), "Results"+pollId.toString());
        return exportService.toJSON(results);
    }

    /*@PostMapping("/toJSONWithPollResult")
    public String createExportJSON(Poll poll, List<PollResult> result){
        return exportService.createExportJSON(poll, result);
    }*/

    public String addHeaders(Long pollId){
        Poll pollToConvert = pollService.getPoll(pollId);
        List<Category> categories = pollToConvert.getCategoryList();
        ListIterator<Category> categoryIterator = categories.listIterator();

        StringBuilder columnNamesList = new StringBuilder(pollToConvert.getPollName()); /*Table headers*/
        /*Structure: PollID, PollName, AnonymityStatus, PollCreator*/
        columnNamesList.append(pollToConvert.getPollId()).append(',').append(pollToConvert.getPollName()).append(',').append(pollToConvert.getAnonymityStatus()).append(',').append(pollToConvert.getPollCreator()).append(',');
        while(categoryIterator.hasNext()) {
            Category singularCategory = categoryIterator.next(); //Page
            for (Question singularQuestion : singularCategory.getQuestionList()) {
                String singularQuestionMessage = singularQuestion.getQuestionMessage();
                columnNamesList.append(",").append(singularQuestionMessage); /*I think it works that way, should give out "PollID, Frage1, Frage2, Frage3, ... regardless of category"*/
            }
        }
        return columnNamesList.toString();
    }

    /*So how does it work?: */
    /*Function iterates over the list of pollResults first filling the first row of Elements with the Questions and then filling the rest with the actual PollResults.*/

    @PostMapping("/answers/{pollId:\\d+}")
    public String convertPollWithResultsToCSV(final @PathVariable Long pollId) {
        Poll pollToConvert = pollService.getPoll(pollId);
        List<Category> categories = pollToConvert.getCategoryList();
        ListIterator<Category> categoryIterator = categories.listIterator();
        String columnNamesList = addHeaders(pollId);
        /*This is what does the work I guess*/

        StringBuilder builder = new StringBuilder();

        /*IMPORTANT: Structure: PollID; PollTaker; PollResultID; LastEditAt; List of Answers*/
        // No need give the headers Like: id, Name on builder.append
        builder.append(columnNamesList).append("\n");
        List<PollResult> results = pollResultService.getPollResults(pollId);
        for (PollResult singularResult : results) {
            ListIterator<Answer> answerIterator = singularResult.getAnswerList().listIterator();
            builder.append(singularResult.getPollId()).append(',').append(singularResult.getPollTaker()).append(',').append(singularResult.getPollResultId()).append(',').append(singularResult.getLastEditAt());
            while (answerIterator.hasNext()) {
                Answer singularAnswer = answerIterator.next();
                /*Iterate over list to make every Answer one column in the csv table*/
                builder.append(answerToCSV(singularAnswer)).append(',');
            }
            builder.append('\n');
        }
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File("PollData.csv"));
            pw.write(builder.toString());
            pw.close();
            return builder.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "Export failed.";
        }
    }

    public String answerToCSV(Answer input) {
        ListIterator<String> answerIterator = input.getGivenAnswerList().listIterator();
        StringBuilder output = new StringBuilder();
        while(answerIterator.hasNext()) {
            String answerForSingularQuestion = answerIterator.next();
            output.append(answerForSingularQuestion).append(",");
        }
        output.append('\n');
        return output.toString();
    }

}

