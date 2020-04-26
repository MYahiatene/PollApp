package gpse.umfrato.domain.question;

import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.poll.Poll;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Question {

    //private static final long serialVersionUID = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Lob
    private String question;

    @ManyToOne
    private Poll poll;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Answer> answerList;


    public Question(final String question,Poll poll) {
        this.question = question;
        this.poll=poll;
    }


}
