package gpse.umfrato.domain.cmd;

import lombok.Data;

import java.util.List;

@Data
public class QuestionCmd {

    private Long pollId;

    private float endValue;

    private float startValue;

    private float stepSize;

    private String belowMessage;

    private String aboveMessage;

    private boolean hideValues;

    private boolean textMultiline;

    private int textMinimum;

    private int textMaximum;

    private boolean textMinBool;

    private boolean textMaxBool;

    private boolean hasConsistencyRelationship;

    private long questionId;

    private long categoryId;

    private String questionMessage;

    private List<String> answerPossibilities;

    private String questionType;

    private boolean userAnswers;

    private int numberOfPossibleAnswers;

    public QuestionCmd(final Long pollId, final float endValue, final float startValue, final float stepSize,
                       final String belowMessage, final String aboveMessage, final boolean hideValues,
                       final boolean textMultiline, final int textMinimum, final int textMaximum,
                       final boolean textMinBool, final boolean textMaxBool,
                       final boolean hasConsistencyRelationship, final long categoryId, final String questionMessage,
                       final List<String> answerPossibilities, final String questionType, final boolean userAnswers,
                       final int numberOfPossibleAnswers) {
        this.pollId = pollId;
        this.endValue = endValue;
        this.startValue = startValue;
        this.stepSize = stepSize;
        this.belowMessage = belowMessage;
        this.aboveMessage = aboveMessage;
        this.hideValues = hideValues;
        this.textMultiline = textMultiline;
        this.textMinimum = textMinimum;
        this.textMaximum = textMaximum;
        this.textMinBool = textMinBool;
        this.textMaxBool = textMaxBool;
        this.hasConsistencyRelationship = hasConsistencyRelationship;
        this.categoryId = categoryId;
        this.questionMessage = questionMessage;
        this.answerPossibilities = answerPossibilities;
        this.questionType = questionType;
        this.userAnswers = userAnswers;
        this.numberOfPossibleAnswers = numberOfPossibleAnswers;
    }
}
