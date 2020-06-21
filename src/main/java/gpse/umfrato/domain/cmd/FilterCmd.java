package gpse.umfrato.domain.cmd;

import lombok.Data;

import java.util.List;

@Data public class FilterCmd {

    private String filterType;

    private boolean invertFilter;

    //DataFilter

    private Long basePollId;

    private List<Long> baseQuestionIds;

    //QuestionFilter
    private Long targetPollId;

    private Long targetQuestionId;

    private List<String> targetAnswerPossibilities;

    //UserFilter
    private Long targetUserId;

    private String targetUserAttribute;

    //StatisticsFilter
    private String function;

    //LogicFilter
    private String logicFunction;

    private String filterA;

    private String filterB;

    //ConsistencyFilter
    private Integer minSuccesses;
}
