package gpse.umfrato.domain.export;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.pollresult.PollResult;
import gpse.umfrato.domain.question.Question;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

@Service
public class ExportServiceImpl implements ExportService {

    @Override
    public String toCSVManual(final Poll poll, final String separator) {
        /*Name, PollID, Creator, Anonymität, Kategorie 1, Kategorie 1, Kategorie 2*/
        /*TestPoll, 1, Tbettmann, 1, Frage 1, Frage 2, Frage 1 aus Kat. 2*/
        final StringBuilder output = new StringBuilder();
        output.append("Name").append(separator).append("PollID").append(separator).append("PollCreator").append(separator).append("Anonymitätsstatus");
        final int amountOfArgumentsBeforeCategories = 4;
        for (final Category category : poll.getCategoryList()) {
            output.append(separator).append(category.getCategoryName()).append(separator).append("Antwortmöglichkeiten");
        }
        output.append('\n');
        output.append(poll.getPollName()).append(separator).append(poll.getPollId()).append(separator).append(poll.getPollCreator()).append(separator).append(poll.getAnonymityStatus());
        for (final Category category : poll.getCategoryList()) {
            for (final Question question : category.getQuestionList()) {
                output.append(separator).append(escapeSpecialCharacters(question.getQuestionMessage())).append(separator);
                for (final String possibility : question.getAnswerPossibilities()) {
                    output.append(' ').append(possibility);
                }
                if (question.getQuestionType().equals("RangeQuestion")) {
                    output.append(question.getStartValue()).append("..").append(question.getEndValue()).append(" in Inkrementen von ").append(question.getStepSize());
                }
                if (question.getQuestionType().equals("SliderQuestion")) {
                    output.append(question.getStartValue()).append("..").append(question.getEndValue()).append(" in Inkrementen von ").append(question.getStepSize());
                }
                output.append('\n');
                /*Needs to be -1 because of output + escapeSpecial... that comma can't go away*/
                output.append(separator.repeat(amountOfArgumentsBeforeCategories - 1));
            }
        }
        output.append('\n');
        return output.toString();
    }

    /*So how does it work?: */
    /*Function iterates over the list of pollResults first filling the first row of Elements with the Questions and then filling the rest with the actual PollResults.*/

    @Override
    public String toCSVManual(final List<PollResult> results, final Poll poll, final String separator) {

        final String columnNamesList = "PollTaker" + separator +  "LastEdit" + addHeaders(poll, separator) + '\n';
        /*This is what does the work I guess*/

        final StringBuilder builder = new StringBuilder();

        /*IMPORTANT: Structure: PollID; PollTaker; PollResultID; LastEditAt; List of Answers*/
        // No need give the headers Like: id, Name on builder.append
        builder.append(columnNamesList).append('\n');
        for (final PollResult singularResult : results) {
            final ListIterator<Answer> answerIterator = singularResult.getAnswerList().listIterator();
            builder.append(singularResult.getPollTaker()).append(separator).append(singularResult.getLastEditAt()).append(separator);
            while (answerIterator.hasNext()) {
                final Answer singularAnswer = answerIterator.next();
                /*Iterate over list to make every Answer one column in the csv table*/
                builder.append(answerToCSV(singularAnswer)).append(separator);
            }
            builder.append('\n');
        }
        return builder.toString();
    }

    private String addHeaders(final Poll pollToConvert, final String separator) {
        final List<Category> categories = pollToConvert.getCategoryList();
        final ListIterator<Category> categoryIterator = categories.listIterator();

        final StringBuilder columnNamesList = new StringBuilder(); /*Table headers*/
        /*Structure: PollID, PollName, AnonymityStatus, PollCreator*/
        while (categoryIterator.hasNext()) {
            final Category singularCategory = categoryIterator.next(); //Page
            for (final Question singularQuestion : singularCategory.getQuestionList()) {
                final String singularQuestionMessage = singularQuestion.getQuestionMessage();
                columnNamesList.append(separator).append(singularQuestionMessage); /*I think it works that way, should give out "PollID, Frage1, Frage2, Frage3, ... regardless of category"*/
            }
        }
        return columnNamesList.toString();
    }


    private String answerToCSV(final Answer input) {
        final ListIterator<String> answerIterator = input.getGivenAnswerList().listIterator();
        final StringBuilder output = new StringBuilder();
        while(answerIterator.hasNext()) {
            final String answerForSingularQuestion = answerIterator.next();
            output.append(escapeSpecialCharacters(answerForSingularQuestion)).append(' ');
        }
        return output.toString();
    }

    private static String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    @Override
    public String toJSON(final Poll result) throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();
            return objectMapper.writeValueAsString(result);
    }

    @Override
    public Poll fromJSONToPoll(final String json) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, Poll.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String toJSONSingularPR(final PollResult result) throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();
            return objectMapper.writeValueAsString(result);
    }

    @Override
    public String toJSON(final List<PollResult> result) throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();
            return objectMapper.writeValueAsString(result);
    }

    @Override
    public List<PollResult> fromJSONToResult(final String json) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, new TypeReference<List<PollResult>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String createExportJSON(final Poll poll, final List<PollResult> result) {
        final JSONObject combined = new JSONObject();
        try {
            final JSONObject pollJSON = new JSONObject();
            pollJSON.getJSONObject(toJSON(poll));
            combined.put("Poll", pollJSON);
            JSONObject resultJSON;
            for (final PollResult resultIndex : result) {
                resultJSON = new JSONObject();
                resultJSON.getJSONObject(toJSONSingularPR(resultIndex));
                combined.put("Results", resultJSON);
            }
            return combined.toString();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
