package gpse.umfrato.domain.question;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This Object represents a question.
 */
@Entity
@Data
@NoArgsConstructor
public class Question {

    // needed for RangeQuestions

    private float endValue;

    private float startValue;

    private float stepSize;

    // needed for Slide and RangeQuestions

    private String belowMessage;

    private String aboveMessage;

    // needed for Slide Questions

    private boolean hideValues = true; // gut so?

    // needed for Text Questions

    private boolean textMultiline = false;

    private int textMinimum;

    private int textMaximum;

    private boolean textMinBool;

    private boolean textMaxBool;

    private boolean hasConsistencyRelationship = false;

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

    @OrderColumn
    @ElementCollection
    private List<String> answerPossibilities = new ArrayList<>();

    private boolean userAnswers = false;

    private int numberOfPossibleAnswers = 1;

    /**
     * This attribute represents the type of question (dropdown, text field, ...).
     */
    private String questionType; //ChoiceQuestion, TextQuestion, RangeQuestion, SliderQuestion

    /**
     * This textQuestion question constructor receives a question message and saves in the question object.
     *
     * @param question      the question message
     * @param textMultiline if you can jump to the next line
     * @param textMinimum   the minimal number of letters needed in a textfield
     * @param textMaximum   the maximal number of letters possible in a textfield
     */
    public Question(final String question, final Boolean textMultiline, final int textMinimum,
                    final int textMaximum) {
        this.questionMessage = question;
        this.questionType = "TextQuestion";
        this.textMultiline = textMultiline;
        this.textMinimum = textMinimum;
        this.textMaximum = textMaximum;
    }

    /**
     * This choiceQuestion constructor receives a question message and saves in the question object.
     *
     * @param question                the question message
     * @param answerPossibilities     a list with all possible answers to this question
     * @param numberOfPossibleAnswers the number of possible answers
     * @param userAnswers             the answers from user
     */
    public Question(final String question, final List<String> answerPossibilities,
                    final Integer numberOfPossibleAnswers, final Boolean userAnswers) {
        this.questionMessage = question;
        this.answerPossibilities = answerPossibilities;
        this.numberOfPossibleAnswers = numberOfPossibleAnswers;
        this.userAnswers = userAnswers;
        this.questionType = "ChoiceQuestion";
    }

    /**
     * This rangeQuestion constructor receives a question message and saves in the question object.
     *
     * @param question     the question message
     * @param endValue     the end Value of a range, for rangeQuestions
     * @param startValue   the start Value of a range, for rangeQuestions
     * @param stepSize     the size of the steps between the start and end value of a rangeQuestion
     * @param belowMessage the message for the meaning of the start value of a range Question
     * @param aboveMessage the message for the meaning of the end value of a range Question
     */
    public Question(final String question, final float endValue, final float startValue,
                    final float stepSize, final String belowMessage, final String aboveMessage) {
        this.questionMessage = question;
        this.questionType = "RangeQuestion";
        this.endValue = endValue;
        this.startValue = startValue;
        this.stepSize = stepSize;
        this.belowMessage = belowMessage;
        this.aboveMessage = aboveMessage;
    }

    /**
     * This sliderQuestion constructor receives a question message and saves in the question object.
     *
     * @param question     the question message
     * @param endValue     the end Value of a range, for rangeQuestions
     * @param startValue   the start Value of a range, for rangeQuestions
     * @param stepSize     the size of the steps between the start and end value of a rangeQuestion
     * @param belowMessage the message for the meaning of the start value of a range Question
     * @param aboveMessage the message for the meaning of the end value of a range Question
     * @param hideValues   for the slide question,if it shows the chosen value
     */
    public Question(final String question, final float endValue, final float startValue, final float stepSize,
                    final String belowMessage, final String aboveMessage, final Boolean hideValues) {
        this.questionMessage = question;
        this.questionType = "SliderQuestion";
        this.endValue = endValue;
        this.startValue = startValue;
        this.stepSize = stepSize;
        this.belowMessage = belowMessage;
        this.aboveMessage = aboveMessage;
        this.hideValues = hideValues;
    }

    /**
     * The question constructor if the question type is SortQuestion.
     * @param questionMessage the message of the question
     * @param answerPossibilities the answer possibilities of this question
     */
    public Question(final String questionMessage, final List<String> answerPossibilities) {
        this.questionMessage = questionMessage;
        this.questionType = "SortQuestion";
        this.answerPossibilities = answerPossibilities;
    }
}

