package gpse.umfrato.domain.export;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.cmd.PollCmd;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.pollresult.PollResult;
import gpse.umfrato.domain.question.Question;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@Service
public class ExportServiceImpl implements ExportService {

    private static final String TEXT_QUESTION = "TextQuestion";
    private static final String RANGE_QUESTION = "RangeQuestion";
    private static final String SLIDER_QUESTION = "SliderQuestion";
    private static final String CHOICE_QUESTION = "ChoiceQuestion";
    private static final String SORT_QUESTION = "SortQuestion";

    /**
     * @param poll Poll welcher zu einer CSV gemacht werden soll
     * @param separator Separator für die CSV-Datei
     * Die Funktion geht den Poll durch und macht daraus eine menschenleserliche CSV-Datei
     * */
    @Override
    public String toCSVManual(final Poll poll, final String separator) {
        /*Name, PollID, Creator, Anonymität, Kategorie 1, Kategorie 1, Kategorie 2*/
        /*TestPoll, 1, Tbettmann, 1, Frage 1, Frage 2, Frage 1 aus Kat. 2*/
        final StringBuilder output = new StringBuilder();
        output.append("Name").append(separator).append("PollID").append(separator).append("PollCreator").
            append(separator).append("Anonymitätsstatus");
        output.append('\n');
        output.append(poll.getPollName()).append(separator).append(poll.getPollId()).append(separator).
            append(poll.getPollCreator()).append(separator).append(poll.getAnonymityStatus()).append('\n').append('\n');

        //String catSeparatorIncrement = "";
        for (final Category category : poll.getCategoryList()) {
            output.append(category.getCategoryName()).append(separator).append("Antwortmöglichkeiten").append('\n');
            for (final Question question : category.getQuestionList()) {
                //output.append(catSeparatorIncrement);
                output.append(escapeSpecialCharacters(question.getQuestionMessage())).
                    append(separator);
                for (final String possibility : question.getAnswerPossibilities()) {
                    output.append(' ').append(possibility);
                }
                if (question.getQuestionType().equals("RangeQuestion")) {
                    output.append(question.getStartValue()).append("..").append(question.getEndValue()).
                        append(" in Inkrementen von ").append(question.getStepSize());
                }
                if (question.getQuestionType().equals("SliderQuestion")) {
                    output.append(question.getStartValue()).append("..").append(question.getEndValue()).
                        append(" in Inkrementen von ").append(question.getStepSize());
                }
                output.append('\n');
                /*Needs to be -1 because of output + escapeSpecial... that comma can't go away*/
                //output.append(separator.repeat(amountOfArgumentsBeforeCategories - 1));
            }
            output.append('\n');
            //catSeparatorIncrement += separator + separator; //,,
        }
        output.append('\n');
        return output.toString();
    }

    /*So how does it work?: */
    /*Function iterates over the list of pollResults first filling the first row of Elements with the Questions and
    then filling the rest with the actual PollResults.*/

    Question getQuestionFromQuestionList(long questionId, List<Question> questionList){
        for(Question q : questionList){
            if(q.getQuestionId().equals(questionId))
                return q;
        }
        return null;
    }

    /**
     * @param poll Die Poll zu denen die PollResults gehören
     * @param results Die Liste an PollResults welche als CSV repräsentiert werden soll
     * @param separator Der Separator für die CSV-Datei
     * Die Funktion geht die Liste an PollResults durch und schreibt die Ergebnisse jeder Frage untereinander
     * */
    @Override
    public String toCSVManual(final List<PollResult> results, final Poll poll, final String separator) {

        final String columnNamesList = "PollTaker" + separator +  "LastEdit" + addHeaders(poll, separator) + '\n';
        /*This is what does the work I guess*/

        final StringBuilder builder = new StringBuilder();

        List<Question> questionList = new ArrayList<>();
        for(Category c : poll.getCategoryList())
            for(Question q : c.getQuestionList())
                questionList.add(q);

        /*IMPORTANT: Structure: PollID; PollTaker; PollResultID; LastEditAt; List of Answers*/
        // No need give the headers Like: id, Name on builder.append
        builder.append(columnNamesList).append('\n');
        for (final PollResult singularResult : results) {
            final ListIterator<Answer> answerIterator = singularResult.getAnswerList().listIterator();
            ZonedDateTime lastEdit = singularResult.getLastEditAt();
            DateTimeFormatter lastEditFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss z");
            builder.append(singularResult.getPollTaker()).append(separator).append(lastEdit.format(lastEditFormatter)).
                append(separator);
            while (answerIterator.hasNext()) {
                final Answer singularAnswer = answerIterator.next();
                /*Iterate over list to make every Answer one column in the csv table*/
                System.out.println(getQuestionFromQuestionList(singularAnswer.getQuestionId(), questionList).getQuestionType());
                if(getQuestionFromQuestionList(singularAnswer.getQuestionId(), questionList).getQuestionType() == CHOICE_QUESTION
                    || getQuestionFromQuestionList(singularAnswer.getQuestionId(), questionList).getQuestionType() == SORT_QUESTION)
                    builder.append(answerToReadableCSV(singularAnswer, getQuestionFromQuestionList(singularAnswer.getQuestionId(), questionList))).append(separator);
                else
                    builder.append(answerToCSV(singularAnswer)).append(separator);
            }
            builder.append('\n');
        }
        return builder.toString();
    }

    /**
     * @param pollToConvert Der Poll für den die Header in der CSV-Datei hinzugefügt werden sollen
     * @param separator Der benutzte Separator
     * */
    private String addHeaders(final Poll pollToConvert, final String separator) {
        final List<Category> categories = pollToConvert.getCategoryList();
        final ListIterator<Category> categoryIterator = categories.listIterator();

        final StringBuilder columnNamesList = new StringBuilder(); /*Table headers*/

        pollToConvert.getCategoryList().get(0).getQuestionList().get(0).getAnswerPossibilities().get(0);
        /*Structure: PollID, PollName, AnonymityStatus, PollCreator*/
        while (categoryIterator.hasNext()) {
            final Category singularCategory = categoryIterator.next(); //Page
            for (final Question singularQuestion : singularCategory.getQuestionList()) {
                final String singularQuestionMessage = singularQuestion.getQuestionMessage();
                columnNamesList.append(separator).append(singularQuestionMessage); /*I think it works that way,
                should give out "PollID, Frage1, Frage2, Frage3, ... regardless of category"*/
            }
        }
        return columnNamesList.toString();
    }

    /**
     * @param input
     * Die Funktion geht die Liste an gegebenen Antworten in einer Antwort durch und macht Listen CSV-Verträglich
     * */
    private String answerToCSV(final Answer input) {
        final ListIterator<String> answerIterator = input.getGivenAnswerList().listIterator();
        final StringBuilder output = new StringBuilder();
        while (answerIterator.hasNext()) {
            final String answerForSingularQuestion = answerIterator.next();
            output.append(escapeSpecialCharacters(answerForSingularQuestion)).append(' ');
        }
        return output.toString();
    }

    private String answerToReadableCSV(Answer answer, Question q){
        final ListIterator<String> answerIterator = answer.getGivenAnswerList().listIterator();
        final StringBuilder output = new StringBuilder();
        while(answerIterator.hasNext()){
            String next = answerIterator.next();
            System.out.println("Answer next: "+next);
            System.out.println("QuestionMessages: "+q.getAnswerPossibilities());
            final String answerForSingularQuestion = q.getAnswerPossibilities().get(Integer.parseInt(next));
            output.append(escapeSpecialCharacters(answerForSingularQuestion)).append(' ');
        }
        return output.toString();
    }

    /**
     * @param data Der Eingabe-String welcher ohne Escape-Character dargestellt werden soll
     * Die Funktion nimmt einen String an und ersetzt die ganzen Escape-Character welche eine CSV kaputt machen würden
     * */
    private static String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    /**
     * @param result Die Poll welche zu einer JSON gemacht werden soll
     * Die Funktion verwandelt eine Poll in eine JSON, dafür wird Jackson benötigt.
     * */
    @Override
    public String toJSON(final Poll result) throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
            objectMapper.findAndRegisterModules();
            return objectMapper.writeValueAsString(result);
    }

    /**
     * @param json Die Eingabe-JSON-Datei
     * Die Funktion nimmt eine JSON entgegen und gibt ein PollCMD mit den enthaltenen Daten zurück.
     * */
    @Override
    public PollCmd fromJSONToPoll(final String json) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper.readValue(json, PollCmd.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param result Das Eingabe-PollResult
     * Die Funktion nimmt ein PollResult entgegen und generiert daraus eine JSON
     * */
    public String toJSONSingularPR(final PollResult result) throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
            objectMapper.findAndRegisterModules();
            return objectMapper.writeValueAsString(result);
    }

    /**
     * @param result Die Eingabeliste an PollResults
     * Die Funktion nimmt eine Liste an PollResults entgegen und generiert daraus eine JSON
     * */
    @Override
    public String toJSON(final List<PollResult> result) throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
            objectMapper.findAndRegisterModules();
            return objectMapper.writeValueAsString(result);
    }

    /**
     * @param json Die Eingabe-JSON
     * Die Funktion nimmt eine JSON entgegen und generiert daraus eine Liste an PollResults, die Funktion wird im Moment nicht genutzt aber vielleicht wird sie mal nütylich
     * */
    @Override
    public List<PollResult> fromJSONToResult(final String json) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper.readValue(json, new TypeReference<List<PollResult>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param poll Eingabe-Poll
     * @param result Eingabe-Liste an PollResults
     * Die Funktion nimmt eine Poll und eine Liste an PollResults entgegen und generiert daraus eine "kombinierte" liste an beidem
     * */
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
