package gpse.umfrato.domain.Evaluation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.pollresult.PollResult;
import gpse.umfrato.domain.question.Question;
import gpse.umfrato.domain.question.QuestionRepository;
import gpse.umfrato.domain.question.QuestionService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.bytebuddy.description.method.ParameterList;

import java.util.ArrayList;
import java.util.List;

public class DiagramData {
private final List<Long> ids = new ArrayList<>();
private final List<String> titles = new ArrayList<>();
private final List<List<String>> answerPossibilities = new ArrayList<>();
private List<List<Integer>> data;
@JsonIgnore
private final QuestionService questionService;

public DiagramData(Poll poll, List<PollResult> results, QuestionService questionService)
{
    this.questionService = questionService;
    loadData(results);
}

public void loadData(List<PollResult> input)
{
    data = new ArrayList<>(); //Liste von Fragen und gegebenen Antworten
    int maxSize = 0;
    for(PollResult pr:input)
    {
        if(pr.getAnswerList().size() > maxSize)
        {
            maxSize = pr.getAnswerList().size();
        }
    }
    for(int i = 0; i < maxSize;i++) //für jede Frage
    {
        data.add(new ArrayList<>());
        answerPossibilities.add(new ArrayList<>());
        for(int j = 0; j < maxSize;j++) //druch alle PollResults
        {
            while(titles.size() <= i)
            {
                titles.add("");
                ids.add(0L);
            }
            if(titles.get(i).isEmpty()) //Frage mit allen Infos anlegen, falls neu
            {
                Question q = questionService.getQuestion(input.get(j).getAnswerList().get(i).getQuestionId());
                titles.set(i,"\"" + q.getQuestionMessage() + "\"");
                ids.set(i,q.getQuestionId());
                for(int k = 0; k < q.getAnswerPossibilities().size();k++)
                {
                    answerPossibilities.get(i).add("\"" + q.getAnswerPossibilities().get(k) + "\"");
                    data.get(i).add(0);
                }
            }
            for(String s:input.get(j).getAnswerList().get(i).getGivenAnswerList()) //und alle gegebenen Antworten
            {
                int value = Integer.parseInt(s);
                data.get(i).set(value, data.get(i).get(value) + 1); //Antwort zählen
            }
        }
    }
}

public String toJSON()
{
    StringBuilder json = new StringBuilder();
    if(titles.size() > 1)
    {
        json.append("[");
    }
    for(int i = 0; i < titles.size();i++)
    {
       json.append("{\"id\": ").append(ids.get(i)).append(",\"title\": ").append(titles.get(i))
           .append(",\"answerPossibilities\": ").append(answerPossibilities.get(i).toString()).append(",\"data\": ")
           .append(data.get(i).toString()).append("}");
       if(i + 1 < titles.size())
       {
           json.append(",");
       }
    }
    if(titles.size() > 1) {
        json.append("]");
    }
    String result = json.toString();
    System.out.println("Ergebniss: " + result);
    return result;
}
}
