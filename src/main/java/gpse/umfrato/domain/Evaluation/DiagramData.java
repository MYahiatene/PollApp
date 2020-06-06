package gpse.umfrato.domain.Evaluation;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class DiagramData {
private final long id;
private final String title;
private final List<String> answerPossibilities;
private final List<Integer> data;

public String toJSON()
{
    int i = 0;
    for(String answer:answerPossibilities)
    {
        answerPossibilities.set(i, '\"' + answer + '\"');
        i++;
    }
    return "{\"id\": " + String.valueOf(id) +
        ",\"title\": \"" + title + "\"" +
        ",\"answerPossibilities\": " + answerPossibilities.toString() +
        ",\"data\": " + data.toString() + "}";
}
}
