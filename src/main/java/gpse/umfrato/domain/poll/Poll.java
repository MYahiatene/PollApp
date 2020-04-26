package gpse.umfrato.domain.poll;

import gpse.umfrato.domain.question.Question;
import gpse.umfrato.domain.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
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

    @OneToOne
    private User creator;

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

    public void addQuestion(Question question) {
        questionList.add(question);
        question.setPoll(this);
    }

    public void removeQuestion(Question question) {
        questionList.remove(question);
        question.setPoll(null);
    }
}
