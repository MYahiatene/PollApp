package gpse.umfrato.domain.evaluation.filter;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class FilterData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long filterId;

    private String filterType;

    private Boolean invertFilter;

    //DataFilter

    private Long basePollId;

    @ElementCollection
    private List<Long> baseQuestionIds;

    private Boolean timeDiagram;

    //QuestionFilter
    private Long targetQuestionId;

    @ElementCollection
    private List<String> targetAnswerPossibilities;

    private Boolean isSlider;

    //UserFilter
    @ElementCollection
    private List<String> userNames;

    //ConsistencyFilter
    private Integer minSuccesses;

    //DateFilter

    private String startDate;

    private String endDate;

}
