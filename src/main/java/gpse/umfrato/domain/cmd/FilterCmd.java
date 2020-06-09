package gpse.umfrato.domain.cmd;

import lombok.Data;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;

import java.util.List;

@Data
public class FilterCmd {

    private String filterType;

    private boolean invertFilter;

    //DataFilter

    private String basePollId;

    private List<String> baseQuestionIds;

    //QuestionFilter
    private String targetPollId;

    private String targetQuestionId;

    private List<String> targetAnswerPossibilities;

    //UserFilter
    private String targetUserId;

    private String targetUserAttribute;

    //StatisticsFilter
    private String function;

    //LogicFilter
    private String logicFunction;

    private String filterA;

    private String filterB;
}
