package gpse.umfrato.domain.answer;

import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Long answerId;


    /**
     * This attribute represents the given answers.
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
    public Answer(final List<String> messageList, final Long questionId) {
        this.givenAnswerList = messageList;
        this.questionId = questionId;
    }


}
