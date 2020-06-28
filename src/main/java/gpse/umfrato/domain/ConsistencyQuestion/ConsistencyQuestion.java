package gpse.umfrato.domain.ConsistencyQuestion;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class ConsistencyQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long consistencyQuestionId;

    private Long pollId;

    private Long question1Id;

    private Boolean question1Slider = false;

    @ElementCollection
    private List<String> answer1Indices;

    private Long question2Id;

    private Boolean question2Slider = false;

    @ElementCollection
    private List<String> answer2Indices;
}
