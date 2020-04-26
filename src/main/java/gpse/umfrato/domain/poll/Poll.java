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
public class Poll implements Serializable {
    private final static long serialVersionUID=5L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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


    @OneToMany(mappedBy = "poll",cascade = CascadeType.ALL)
    private List<Question> questionList;

    public Poll(final String pollname) {
        this.pollname = pollname;
        this.pollStatus = 0;
        this.pollCreatedAt = LocalDateTime.now();

    }
}
