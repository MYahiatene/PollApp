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

    public QuestionCmd(Long pollId, float endValue, float startValue, float stepSize, String belowMessage,
                       String aboveMessage, boolean hideValues, boolean textMultiline, int textMinimum, int textMaximum,
                       boolean textMinBool, boolean textMaxBool, boolean hasConsistencyRelationship, long categoryId,
                       String questionMessage, List<String> answerPossibilities, String questionType,
                       boolean userAnswers, int numberOfPossibleAnswers) {
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
