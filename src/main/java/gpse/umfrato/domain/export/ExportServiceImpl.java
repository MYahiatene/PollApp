package gpse.umfrato.domain.export;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.pollresult.PollResult;
import gpse.umfrato.domain.question.Question;
import org.json.JSONObject;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.List;

@Service
public class ExportServiceImpl implements ExportService {

    @Override
    public String toCSVManual(Poll poll) {
        /*Name, PollID, Creator, Anonymität, Kategorie 1, Kategorie 1, Kategorie 2*/
        /*TestPoll, 1, Tbettmann, 1, Frage 1, Frage 2, Frage 1 aus Kat. 2*/
        StringBuilder output = new StringBuilder();
        output.append("Name,PollID,PollCreator,Anonymitätsstatus");
        int amountOfArgumentsBeforeCategories = 4;
        for (Category category : poll.getCategoryList()) {
            output.append(',').append(category.getCategoryName()).append(',').append("Antwortmöglichkeiten");
        }
        output.append('\n');
        output.append(poll.getPollName()).append(',').append(poll.getPollId()).append(',').append(poll.getPollCreator()).append(',').append(poll.getAnonymityStatus());
        for (Category category : poll.getCategoryList()) {
            for (Question question : category.getQuestionList()) {
                output.append(',').append(escapeSpecialCharacters(question.getQuestionMessage())).append(',');
                for (String possibility : question.getAnswerPossibilities()) {
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
                output.append(",".repeat(amountOfArgumentsBeforeCategories - 1));
            }
        }
        output.append('\n');
        return output.toString();
    }

    @Override
    public String toJSON(Poll result) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();
            return objectMapper.writeValueAsString(result);
    }

    @Override
    public Poll fromJSONToPoll(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, Poll.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String toJSONSingularPR(PollResult result) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();
            return objectMapper.writeValueAsString(result);
    }

    @Override
    public String toJSON(List<PollResult> result) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();
            return objectMapper.writeValueAsString(result);

    }

    @Override
    public List<PollResult> fromJSONToResult(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, new TypeReference<List<PollResult>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String createExportJSON(Poll poll, List<PollResult> result) {
        JSONObject combined = new JSONObject();
        try {
            JSONObject pollJSON = new JSONObject();
            pollJSON.getJSONObject(toJSON(poll));
            combined.put("Poll", pollJSON);
            JSONObject resultJSON;
            for (PollResult resultIndex : result) {
                resultJSON = new JSONObject();
                resultJSON.getJSONObject(toJSONSingularPR(resultIndex));
                combined.put("Results", resultJSON);
            }
            return combined.toString();
        } catch(JsonProcessingException e){
            e.printStackTrace();
            return null;
        }
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
