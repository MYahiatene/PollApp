package gpse.umfrato.domain.answer;

import gpse.umfrato.domain.answer.question.Question;
import gpse.umfrato.domain.answer.user.User;
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

    @ManyToOne
    @Getter
    private Question question;

    protected Answer() {

    }

    public Answer(final String answer, final User user, final String answertype,Question question) {
        this.answer = answer;
        this.user = user;
        this.answertype = answertype;
        this.question = question;
    }


}
