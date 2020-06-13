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
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pollResultId;

    private Long pollId;

    private String pollTaker;

    private String lastEditAt = "13.06.2020";

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Answer> answerList = new ArrayList<>();

    public PollResult(final Long pollId, final String username) {
        this.pollId = pollId;
        this.pollTaker = username;
    }
}
