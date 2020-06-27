package gpse.umfrato.domain.export;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.pollresult.PollResult;
import gpse.umfrato.domain.question.Question;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ExportServiceImpl implements ExportService {

    @Override
    public String toCSVManual(Poll poll) {
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
    @Override
    public String toJSON(Poll result) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();
        try {
            return objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new Exception("Serialisierung fehlgeschlagen");
        }
    }

    @Override
    public Poll fromJSONToPoll(String json) {
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

    @Override
    public String toJSON(PollResult result) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();
        try {
            return objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new Exception("Serialisierung fehlgeschlagen");
        }
    }

    @Override
    public PollResult fromJSONToResult(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PollResult pResult = objectMapper.readValue(json, PollResult.class);
            return pResult;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String createExportJSON(Poll poll, List<PollResult> result) {
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

    public static String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

}
