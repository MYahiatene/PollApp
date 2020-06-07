package gpse.umfrato.domain.cmd;

import lombok.Data;

import java.util.List;

@Data
public class AnswerCmd {

    private String username;

    private String questionId;

    private List<String> answerList;

    private String answerId;

    private String pollId;
}
