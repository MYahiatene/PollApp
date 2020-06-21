package gpse.umfrato.domain.cmd;

import lombok.Data;

import java.util.List;

@Data
public class ConsistencyQuestionCmd {

    private Long consistencyQuestionId;

    private Long pollId;

    private Long question1Id;

    private List<String> answer1Indices;

    private Long question2Id;

    private List<String> answer2Indices;
}
