package gpse.umfrato.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private long id;

    @Column
    @Getter
    private String answertype;

    @Column
    @Lob
    @Getter
    @Setter
    private String answer;

    @OneToOne
    @Getter
    @Setter
    private User user;

    protected Answer() {

    }

    public Answer(final String answer, final User user, final String answertype) {
        this.answer = answer;
        this.user = user;
        this.answertype = answertype;
    }


}
