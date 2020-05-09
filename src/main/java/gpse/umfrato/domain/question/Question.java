package gpse.umfrato.domain.question;

import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.poll.Poll;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * This Object represents a question.
 */
@Entity
@Data
@NoArgsConstructor
public class Question {

    //private static final long serialVersionUID = 1;

    /**
     * This attribute represents an unique id from the object question.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * This attribute represents the question message.
     */
    @Lob
    private String questionMessage;

    /**
     * This attribute represents the poll where the question is assigned.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Poll poll;

    /**
     * This attribute represents a list with all answers for this question.
     */
    @OneToMany(cascade = CascadeType.ALL)
    private List<Answer> answerList;

    /**
     * This constructor receives a question message and saves in the question object.
     *
     * @param question the question message which is given
     */
    public Question(final String question) {
        this.questionMessage = question;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionMessage() {
        return questionMessage;
    }

    public void setQuestionMessage(String questionMessage) {
        this.questionMessage = questionMessage;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    public List<Answer> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<Answer> answerList) {
        this.answerList = answerList;
    }
}
