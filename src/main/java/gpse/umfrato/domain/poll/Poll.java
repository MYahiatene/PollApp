package gpse.umfrato.domain.poll;

import gpse.umfrato.domain.question.Question;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Poll {

    @Id
    @GeneratedValue
    private Long id;

    private String pollcreator;

    private LocalDateTime pollCreatedAt;

    private LocalDateTime lastEditAt;

    // use Instant instead , cause of time zone
    private LocalDateTime activatedAt;

    private LocalDateTime deactivatedAt;

    private String anonymityStatus;

    private String pollname;

    private int pollStatus;


    @OneToMany(cascade = CascadeType.ALL)
    private List<Question> questionList=new ArrayList<>();

    public Poll(String pollName) {
        this.pollname = pollName;
    }

}
