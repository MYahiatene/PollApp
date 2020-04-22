package gpse.umfrato.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private String pollname;

    @Column
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
    }
}
