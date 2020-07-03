package gpse.umfrato.domain.cmd;

import lombok.Data;

import java.util.List;

/**
 * The command design class for answer.
 */
@Data
public class AnswerCmd {

    private String username;

    private Long questionId;

    private List<String> answerList;

    private Long answerId;

    private Long pollId;
}
