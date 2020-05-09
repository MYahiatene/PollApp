package gpse.umfrato.domain.cmd;

import lombok.Data;

@Data
public class QuestionCmd {

    private String id;

    private String question;


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
