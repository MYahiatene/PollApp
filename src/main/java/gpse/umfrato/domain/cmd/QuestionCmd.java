package gpse.umfrato.domain.cmd;

import lombok.Data;

import java.util.List;

@Data
public class QuestionCmd {

    private String pollId;

    private String questionMessage;

    private List<String> answerPossibilities;

    private String questionType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
