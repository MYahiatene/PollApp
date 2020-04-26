package gpse.umfrato.domain.answer;

import gpse.umfrato.domain.question.Question;
import gpse.umfrato.domain.user.User;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    private String answertype;

    @Lob
    private String answer;

    @OneToOne
    private User user;

    @ManyToOne
    private Question question;


    public Answer(final String answer, final User user, final String answertype,Question question) {
        this.answer = answer;
        this.user = user;
        this.answertype = answertype;
        this.question = question;
    }


}
