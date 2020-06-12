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
private final List<String> medians = new ArrayList<>();
private final List<String> modes = new ArrayList<>();
@JsonIgnore private final QuestionService questionService;

public DiagramData(Poll poll, List<PollResult> results, QuestionService questionService) {
    this.questionService = questionService;
    loadData(results);
}

public void loadData(List<PollResult> input) {
    data = new ArrayList<>(); //Liste von Fragen und gegebenen Antworten
    int maxSize = 0;
    for (PollResult pr: input)
    {
        if (pr.getAnswerList().size() > maxSize)
        {
            maxSize = pr.getAnswerList().size();
        }
    }
    //für jede Frage
    for (int i = 0; i < maxSize; i++)
    {
        data.add(new ArrayList<>());
        answerPossibilities.add(new ArrayList<>());
        //druch alle PollResults
        for (PollResult pr: input)
        {
            while (questionTitles.size() <= i)
            {
                questionTitles.add("");
                ids.add(0L);
            }
            //Frage mit allen Infos anlegen, falls neu
            if (questionTitles.get(i).isEmpty())
            {
                Question q = questionService.getQuestion(pr.getAnswerList().get(i).getQuestionId());
                questionTitles.set(i, "\"" + q.getQuestionMessage() + "\"");
                ids.set(i, q.getQuestionId());
                for (int k = 0; k < q.getAnswerPossibilities().size(); k++)
                {
                    answerPossibilities.get(i).add("\"" + q.getAnswerPossibilities().get(k) + "\"");
                    data.get(i).add(0);
                }
            }
            //und alle gegebenen Antworten
            for (String s: pr.getAnswerList().get(i).getGivenAnswerList())
            {
                int value = Integer.parseInt(s);
                //Antwort zählen
                data.get(i).set(value, data.get(i).get(value) + 1);
            }
        }
    }
    for(int i = 0; i < data.size(); i++)
    {
        int size = 0;
        List<Integer> maxima = new ArrayList<>();
        int max = 0;
        //Häufigste Antwort und Menge der Antworten raussuchen
        for (int j = 0; j < data.get(i).size(); j++)
        {
            size += data.get(i).get(j);
            if (data.get(i).get(j) > max)
            {
                maxima.clear();
                maxima.add(j);
                max = data.get(i).get(j);
            }
            else if(data.get(i).get(j) == max)
            {
                maxima.add(j);
            }
        }
        StringBuilder modeText = new StringBuilder();
        for(Integer j:maxima)
        {
            modeText.append(answerPossibilities.get(i).get(j).substring(1, answerPossibilities.get(i).get(j).length() - 1)).append(" / ");
        }
        modeText.replace(modeText.lastIndexOf(" / "),modeText.length(),"");
        modes.add("\"" +  modeText.toString() + "\"");
        //Median ist an mittlerer Stelle
        int medianPos = size / 2;
        relative.add(new ArrayList<>());
        for (int j = 0; j < data.get(i).size(); j++)
        {
            //Relative Häufigkeiten nur Summe vor Division
            relative.get(i).add(data.get(i).get(j).doubleValue() / (double) size);
            medianPos -= data.get(i).get(j);
            // [1,1,1,1,1,1,2,2,2,3,3,3,4,6](originalDaten) => [0,6,3,3,1,0,1](data)
            // => 14(data.size) => 7(/2) => 1(-6) => -2(-3) => 2(Position) => Median
            if(medianPos <= 0 && medians.size() <= i)
            {
                if(medianPos == 0 && j < answerPossibilities.get(i).size())
                {
                    medians.add(answerPossibilities.get(i).get(j).substring(0,answerPossibilities.get(i).get(j).length() - 1)
                        + " / " + answerPossibilities.get(i).get(j + 1).substring(1));
                }
                else
                {
                    medians.add(answerPossibilities.get(i).get(j));
                }
            }
        }
    }
}

public String toJSON() {
    StringBuilder json = new StringBuilder();
    json.append("[");
    for(int i = 0; i < questionTitles.size(); i++)
    {
        json.append("{\"id\": ").append(ids.get(i))
            .append(",\"title\": ").append(questionTitles.get(i))
            .append(",\"answerPossibilities\": ").append(answerPossibilities.get(i).toString())
            .append(",\"data\": ").append(data.get(i).toString())
            .append(",\"calculated\": {")
            .append("\"relative\": ").append(relative.get(i).toString())
            .append(",\"median\": ").append(medians.get(i))
            .append(",\"mode\": ").append(modes.get(i))
            .append("}}");
        if(i + 1 < questionTitles.size())
        {
            json.append(",");
        }
    }
    json.append("]");
    return json.toString();
}
}
