package gpse.umfrato.domain.pollresult;

import gpse.umfrato.domain.answer.Answer;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class PollResult {
    /**
     * This attribute is an unique id from the object pollResult.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pollResultId;

    private Long pollId;

    private String pollTaker;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Answer> answerList = new ArrayList<>();

    public PollResult(final Long pollId, final String username) {
        this.pollId = pollId;
        this.pollTaker = username;
    }
}
