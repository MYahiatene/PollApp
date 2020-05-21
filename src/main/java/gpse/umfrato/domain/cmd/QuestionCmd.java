package gpse.umfrato.domain.cmd;

import lombok.Data;

import java.util.List;

@Data
public class QuestionCmd {

    private String pollId;

    private String questionMessage;

    private List<String> answerPossibilities;

    private String questionType;

}
