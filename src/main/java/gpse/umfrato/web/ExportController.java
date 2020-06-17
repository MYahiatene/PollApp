package gpse.umfrato.web;

import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.pollresult.PollResult;
import gpse.umfrato.domain.pollresult.PollResultService;
import gpse.umfrato.domain.question.Question;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExportController {
    private final PollService pollService;
    private final PollResultService pollResultService;

    public ExportController(PollService pollService, PollResultService pollResultService) {
        this.pollService = pollService;
        this.pollResultService = pollResultService;
    }

    public String convertPollWithResultsToCSV(String pollID){
        Poll pollToConvert = pollService.getPoll(pollID);
        List<Category> categories = pollToConvert.getCategoryList();
        ListIterator<Category> categoryIterator = categories.listIterator();

        String columnNamesList = pollToConvert.getPollName(); /**Table headers*/
        while(categoryIterator.hasNext()) {
            Category singularCategory = categoryIterator.next(); //Page
            ListIterator<Question> questionIterator = singularCategory.getQuestionList().listIterator();
            while(questionIterator.hasNext()) {
                Question singularQuestion = questionIterator.next();
                String singularQuestionMessage = singularQuestion.getQuestionMessage();
                columnNamesList += "," + singularQuestionMessage; /**I think it works that way, should give out "PollID, Frage1, Frage2, Frage3, ... regardless of category"*/
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
        List<PollResult> results = pollResultService.getPollResults(Long.parseLong(pollID));
        ListIterator<PollResult> resultIterator = results.listIterator();
        while(resultIterator.hasNext()) {
            PollResult singularResult = resultIterator.next();
            ListIterator<Answer> answerIterator = singularResult.getAnswerList().listIterator();
            while(answerIterator.hasNext()) {
                Answer singularAnswer = answerIterator.next();
                /**Iterate over list to make every Answer one column in the csv table*/
                builder.append(answerToCSV(singularAnswer));
            }
        }

        /*builder.append("1"+",");
        builder.append("Chola");
        builder.append('\n');*/
        pw.write(builder.toString());
        pw.close();
        System.out.println("done!");

        return builder.toString();
    }

    public String answerToCSV(Answer input){
        ListIterator<String> answerIterator = input.getGivenAnswerList().listIterator();
        String output = "";
        while(answerIterator.hasNext()){
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
