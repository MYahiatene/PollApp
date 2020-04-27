package gpse.umfrato.domain.cmd;

import lombok.Data;

@Data
public class AnswerCmd {

    private String questionId;

    private String answerId;

    private String answerType;

    private String answer;

    private String username;

}
