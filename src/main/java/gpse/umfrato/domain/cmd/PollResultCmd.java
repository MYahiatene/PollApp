package gpse.umfrato.domain.cmd;

import gpse.umfrato.domain.answer.Answer;
import lombok.Data;

import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Data
public class PollResultCmd {

    private Long pollResultId;

    private Long pollId;

    private String pollTaker;

    private String lastEditAt;

    private Boolean participatedPoll;

    @OneToMany/*(fetch = FetchType.EAGER, cascade = CascadeType.ALL)*/
    private List<Answer> answerList = new ArrayList<>();
}
