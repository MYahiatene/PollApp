package gpse.umfrato.domain.cmd;

import lombok.Data;

import java.util.List;

/**
 * The command design class for questions.
 */
@Data
public class QuestionCmd {

    private long pollId;

    private long categoryId;

    private long questionId;

    private double endValue;

    private double startValue;

    private double stepSize;

    private String belowMessage;

    private String aboveMessage;

    private boolean hideValues;

    private boolean textMultiline;

    private int textMinimum;

    private int textMaximum;

    private boolean textMinBool;

    private boolean textMaxBool;

    private boolean hasConsistencyRelationship;

    private boolean dropDown;

    private String questionMessage;

    private List<String> answerPossibilities;

    private String questionType;

    private boolean userAnswers;

    private int numberOfPossibleAnswers;

    private Long categoryType;
}
