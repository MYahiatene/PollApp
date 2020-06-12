package gpse.umfrato.domain.Evaluation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.pollresult.PollResult;
import gpse.umfrato.domain.question.Question;
import gpse.umfrato.domain.question.QuestionService;

import java.util.*;

public class DiagramData {
private final List<Long> ids = new ArrayList<>();
private final List<String> questionTitles = new ArrayList<>();
private final List<List<String>> answerPossibilities = new ArrayList<>();
private List<List<Integer>> data;
private final List<List<Double>> relative = new ArrayList<>();
private final List<Integer> medians = new ArrayList<>();
private final List<Integer> modes = new ArrayList<>();
@JsonIgnore private final QuestionService questionService;

public DiagramData(Poll poll, List<PollResult> results, QuestionService questionService) {
    this.questionService = questionService;
    loadData(results);
}

public void loadData(List<PollResult> input) {
    data = new ArrayList<>(); //Liste von Fragen und gegebenen Antworten
    int maxSize = 0;
    for (PollResult pr: input) {
        if (pr.getAnswerList().size() > maxSize) {
            maxSize = pr.getAnswerList().size();
        }
    }
    //für jede Frage
    for (int i = 0; i < maxSize; i++) {
        data.add(new ArrayList<>());
        answerPossibilities.add(new ArrayList<>());
        //druch alle PollResults
        for (PollResult pr: input) {
            while (questionTitles.size() <= i) {
                questionTitles.add("");
                ids.add(0L);
            }
            //Frage mit allen Infos anlegen, falls neu
            if (questionTitles.get(i).isEmpty()) {
                Question q = questionService.getQuestion(pr.getAnswerList().get(i).getQuestionId());
                questionTitles.set(i, "\"" + q.getQuestionMessage() + "\"");
                ids.set(i, q.getQuestionId());
                for (int k = 0; k < q.getAnswerPossibilities().size(); k++) {
                    answerPossibilities.get(i).add("\"" + q.getAnswerPossibilities().get(k) + "\"");
                    data.get(i).add(0);
                }
            }
            //und alle gegebenen Antworten
            for (String s: pr.getAnswerList().get(i).getGivenAnswerList()) {
                int value = Integer.parseInt(s);
                //Antwort zählen
                data.get(i).set(value, data.get(i).get(value) + 1);
            }
        }
    }
    for (int i = 0; i < data.size(); i++) {
        int size = 0;
        modes.add(0);
        int max = 0;
        for (int j = 0; j < data.get(i).size(); j++) {
            size += data.get(i).get(j);
            if (data.get(i).get(j) > max) {
                max = data.get(i).get(j);
                modes.set(i, j);
            }
        }
        int medianPos = size / 2;
        relative.add(new ArrayList<>());
        for (int j = 0; j < data.get(i).size(); j++) {
            relative.get(i).add(data.get(i).get(j).doubleValue() / (double) size);
            medianPos -= data.get(i).get(j);
            if (medianPos <= 0 && medians.size() <= i) {
                medians.add(j);
            }
        }
    }
}

public String toJSON() {
    StringBuilder json = new StringBuilder();
    if (questionTitles.size() > 1) {
        json.append("[");
    }
    for (int i = 0; i < questionTitles.size(); i++) {
        json.append("{\"id\": ").append(ids.get(i))
            .append(",\"title\": ").append(questionTitles.get(i))
            .append(",\"answerPossibilities\": ").append(answerPossibilities.get(i).toString())
            .append(",\"data\": ").append(data.get(i).toString())
            .append(",\"calculated\": {")
            .append("\"relative\": ").append(relative.get(i).toString())
            .append(",\"median\": ").append(answerPossibilities.get(i).get(medians.get(i)))
            .append(",\"mode\": ").append(answerPossibilities.get(i).get(modes.get(i)))
            .append("}}");
        if (i + 1 < questionTitles.size()) {
            json.append(",");
        }
    }
    if (questionTitles.size() > 1) {
        json.append("]");
    }
    return json.toString();
}
}
