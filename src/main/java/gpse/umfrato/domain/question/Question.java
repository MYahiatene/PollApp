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

    /**
     * This attribute represents an unique id from the object question.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long questionId;

    private Long categoryId;
    /**
     * This attribute represents the question message.
     */
    @Lob
    private String questionMessage;

    @ElementCollection
    private List<String> answerPossibilities;

    /**
     * This attribute represents the type of question (dropdown, text field, ...).
     */
    private String questionType;

    private int numberOfPossibleAnswers;
    /**
     * This constructor receives a question message and saves in the question object.
     * @param question the question message
     * @param answerPossibilities a list with all possible answers to this question
     * @param questionType the type how the question should be answered
     */
    public Question(final String question, final List<String> answerPossibilities, final String questionType) {
        this.questionMessage = question;
        this.answerPossibilities = answerPossibilities;
        this.questionType = questionType;
    }
}

