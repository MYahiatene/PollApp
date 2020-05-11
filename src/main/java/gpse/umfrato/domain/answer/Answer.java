package gpse.umfrato.domain.answer;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /**
     * This attribute represents the message of the answer.
     */
    @Lob
    private String answerMessage;

    /**
     * This attribute represents the username of the user who created the answer.
     */
    private String username;

    /**
     * This attribute represents the question id where the answer is assigned.
     */
    private Long questionId;

    /**
     * This constructor receives some data for the answer object and saves them in the answer object.
     *
     * @param message    the answer message
     * @param questionId the question id where the answer is assigned
     * @param username   the username of the user who created the answer
     */
    public Answer(final String message, final String questionId, final String username) {
        this.answerMessage = message;
        this.username = username;
        this.questionId = Long.valueOf(questionId);
    }


}
