package gpse.umfrato.domain.cmd;

import gpse.umfrato.domain.question.Question;
import lombok.Data;

import javax.persistence.*;
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

    private boolean hasConsistencyRelationship;

    private long questionId;

    private long categoryId;

    private String questionMessage;

    private List<String> answerPossibilities;

    private String questionType;

    private boolean userAnswers;

    private int numberOfPossibleAnswers;

    public Question getQuestion() {
        Question question = null;
        switch (questionType) {
            case "TextQuestion":
                question = new Question(questionMessage, textMultiline, textMinimum, textMaximum);
                question.setQuestionId(questionId);
                question.setCategoryId(categoryId);
                return question;
            case "RangeQuestion":
                question = new Question(questionMessage, (endValue == 0.0f ? 5.0f : stepSize), startValue,
                    (stepSize == 0.0f ? 1.0f : stepSize), (belowMessage == null ? "" : belowMessage),
                    (aboveMessage == null ? "" : aboveMessage));
                question.setQuestionId(questionId);
                question.setCategoryId(categoryId);
                question.setHasConsistencyRelationship(hasConsistencyRelationship);
                return question;
            case "SliderQuestion":
                question = new Question(questionMessage, (endValue == 0.0f ? 1.0f : stepSize), startValue,
                    (stepSize == 0.0f ? 0.1f : stepSize), (belowMessage == null ? "" : belowMessage),
                    (aboveMessage == null ? "" : aboveMessage), hideValues);
                question.setQuestionId(questionId);
                question.setCategoryId(categoryId);
                return question;
            case "ChoiceQuestion":
                question = new Question(questionMessage, answerPossibilities);
                question.setQuestionId(questionId);
                question.setCategoryId(categoryId);
                question.setNumberOfPossibleAnswers(numberOfPossibleAnswers);
                question.setUserAnswers(userAnswers);
                question.setHasConsistencyRelationship(hasConsistencyRelationship);
                return question;
            default: return null;
        }

    }

}
