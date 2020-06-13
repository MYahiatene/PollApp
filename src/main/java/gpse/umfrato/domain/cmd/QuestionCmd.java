package gpse.umfrato.domain.cmd;

import lombok.Data;

import java.util.List;

@Data
public class QuestionCmd {

    private String pollId;

    private String questionId;

    private String categoryId;

    private String questionMessage;

    private List<String> answerPossibilities;

    private String questionType;
}
