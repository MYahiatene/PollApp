package gpse.umfrato.domain.question;

import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.poll.Poll;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.transaction.Transactional;
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

    @ManyToOne(fetch = FetchType.LAZY)
    private Poll poll;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Answer> answerList;

    public void addAnswer(Answer answer) {
        answerList.add(answer);
        answer.setQuestion(this);
    }

    public void removeAnswer(Answer answer) {
        answerList.remove(answer);
        answer.setQuestion(null);
    }
    public Question(final String question,Poll poll) {
        this.question = question;
        this.poll=poll;
    }


}
