package gpse.umfrato.domain;

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

    @Column
    @Getter
    @Setter
    @OneToMany
    private List<Question> questionList = new ArrayList<>();

    protected Poll() {

    }

    public Poll(final String pollname) {
        this.pollname = pollname;
        this.pollStatus = 0;
        this.pollCreatedAt = LocalDateTime.now();

    }
}
