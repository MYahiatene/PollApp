package gpse.umfrato.domain.cmd;

import lombok.Data;

import java.util.List;

@Data
public class ConsistencyQuestionCmd {

    private Long consistencyQuestionId;

    private Long pollId;

    private Long question1Id;

    private List<Long> answer1Ids;

    private Long question2Id;

    private List<Long> answer2Ids;
}
