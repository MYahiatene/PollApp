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

    @ElementCollection
    private List<Long> answer1Ids;

    private Long question2Id;

    @ElementCollection
    private List<Long> answer2Ids;
}
