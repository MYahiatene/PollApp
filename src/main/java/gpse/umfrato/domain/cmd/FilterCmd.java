package gpse.umfrato.domain.cmd;

import lombok.Data;

import java.util.List;

@Data
public class FilterCmd {

    private String filterType;

    private Boolean invertFilter;

    //DataFilter
    private Long basePollId;

    private List<Long> baseQuestionIds;

    private Boolean timeDiagram;

    //QuestionFilter
    private Long targetQuestionId;

    private List<String> targetAnswerPossibilities;

    private Boolean isSlider;

    //UserFilter
    private List<String> userNames;

    //ConsistencyFilter
    private Integer minSuccesses;

    //DateFilter
    private String startDate;

    private String endDate;
}
