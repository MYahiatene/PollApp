package gpse.umfrato.domain.answer.poll;

import gpse.umfrato.domain.answer.question.Question;
import gpse.umfrato.domain.answer.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Column
    private Long id;

    @Column
    @Getter
    private String pollcreator;

    @Column
    @Getter
    private LocalDateTime pollCreatedAt;

    @OneToOne
    @Getter
    private User creator;

    @Column
    @Getter
    private LocalDateTime lastEditAt;

    @Column
    @Getter
    @Setter
    private LocalDateTime activatedAt;

    @Column
    @Getter
    private LocalDateTime deactivatedAt;

    @Column
    @Getter
    private String anonymityStatus;

    //last editor maybe

    @Column
    @Getter
    private String pollname;

    @Column
    @Getter
    private int pollStatus;

    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL)
    private List<Question> questionList = new ArrayList<>();

    protected Poll() {

    }

    public Poll(final String pollname) {
        this.pollname = pollname;
        this.pollStatus = 0;
        this.pollCreatedAt = LocalDateTime.now();

    }
}
