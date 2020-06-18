package gpse.umfrato.web;

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
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    private static final String OBJECT_LIST_SAMPLE = "./object-list-sample.csv";


    public static void pollToCSV(String filePath, Poll poll) {
        // create fle with given filePath
        File file = new File(filePath);
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);

            // adding header to csv
            // is hideValue, textMultiline, textMInBool, textMaxBool, hasConsistencyRelationship, userAnswers important,
            // there are no getters for them available
            String[] header = { "questionId", "categoryId", "questionMessage", "questionType", "answerPossibilities",
                "numberOfPossibleAnswers", "endValue", "startValue", "stepSize", "belowMessage", "aboveMessage",
                "textMinimum", "textMaximum"};
            writer.writeNext(header);

            // add questions of the poll to csv
            for (Question question : poll.getQuestionList()) {
                List<String> list = new ArrayList<>();
                list.add(question.getQuestionId().toString());
                list.add(question.getCategoryId().toString());
                list.add(question.getQuestionMessage());
                list.add(question.getQuestionType());
                list.add(question.getAnswerPossibilities().toString());
                list.add(String.valueOf(question.getNumberOfPossibleAnswers()));
                list.add(String.valueOf(question.getEndValue()));
                list.add(String.valueOf(question.getStartValue()));
                list.add(String.valueOf(question.getStepSize()));
                list.add(question.getBelowMessage());
                list.add(question.getAboveMessage());
                list.add(String.valueOf(question.getTextMinimum()));
                list.add(String.valueOf(question.getTextMaximum()));

                String[] endData = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {

                    // Assign each value to String array
                    endData[i] = list.get(i);
                }
                writer.writeNext(endData);
            }

            // closing writer connection
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String addHeaders(String pollId) {
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
    public String convertPollWithResultsToCSV(final @PathVariable String pollId) {
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
        List<PollResult> results = pollResultService.getPollResults(Long.parseLong(pollId));
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

    public String convertReadableQuestionsToCSV(final @PathVariable String pollId) {
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
