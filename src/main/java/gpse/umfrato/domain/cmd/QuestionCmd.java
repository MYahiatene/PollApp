package gpse.umfrato.domain.cmd;

import lombok.Data;

import java.util.List;

@Data
public class QuestionCmd {

    private String id;

    private String question;

    private List<String> answerPossibilities;

    private String questionType;
}
