package gpse.umfrato.domain.answer;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.engine.internal.Cascade;

import javax.persistence.*;
import java.util.List;

/**
 * This object represents a answer.
 */
@Entity
@Data
@NoArgsConstructor
public class Answer {

    /**
     * This attribute is an unique id from the object answer.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    /**
     * This attribute represents the message of the answer.
     */
    @ElementCollection
    private List<String> givenAnswerList;

    /**
     * This attribute represents the question id where the answer is assigned.
     */
    private Long questionId;

    /**
     * This constructor receives some data for the answer object and saves them in the answer object.
     *
     * @param messageList    the answer message
     * @param questionId the question id where the answer is assigned
     */
    public Answer(final List<String> messageList, final String questionId) {
        this.givenAnswerList = messageList;
        this.questionId = Long.valueOf(questionId);
    }


}
