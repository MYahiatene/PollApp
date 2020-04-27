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
    private Long id;

    @Lob
    private String question;

    @ManyToOne(fetch = FetchType.LAZY)
    private Poll poll;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Answer> answerList;

    public Question(final String question) {
        this.question = question;
    }


}
