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

    private int endValue;

    private int startValue;

    private int stepSize;

    private String belowMessage;

    private String aboveMessage;

    private Boolean hideValues;

    /**
     * This attribute represents the index of the question inside a category
     */
    private int questionIndex;

    private Boolean textMultiline;

    private int textMinimum;

    private int textMaximum;

    /**
     * This constructor receives a question message and saves in the question object.
     * @param question the question message
     * @param answerPossibilities a list with all possible answers to this question
     * @param questionType the type how the question should be answered
     */
    public Question(final String question, final List<String> answerPossibilities, final String questionType,
                    final int endValue, final int startValue, final int stepSize, final String belowMessage,
                    final String aboveMessage, final Boolean hideValues, final int questionIndex,
                    final Boolean textMultiline, final int textMinimum, final int textMaximum) {
        this.questionMessage = question;
        this.answerPossibilities = answerPossibilities;
        this.questionType = questionType;
        this.endValue = endValue;
        this.startValue = startValue;
        this.stepSize = stepSize;
        this.belowMessage = belowMessage;
        this.aboveMessage = aboveMessage;
        this.hideValues = hideValues;
        this.questionIndex = questionIndex;
        this.textMultiline = textMultiline;
        this.textMinimum = textMinimum;
        this.textMaximum = textMaximum;
    }
}

