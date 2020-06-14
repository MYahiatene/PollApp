package gpse.umfrato.domain.cmd;

import gpse.umfrato.domain.question.Question;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
public class QuestionCmd {

    private String pollId;

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

    private int questionIndex;

    private boolean userAnswers;

    private int numberOfPossibleAnswers;

    public Question getQuestion() {
        final Question question = new Question(questionMessage, answerPossibilities, questionType,0, 0, 0, null, null, false, 1, false, 10, 100);
        question.setQuestionId(questionId);
        question.setCategoryId(categoryId);
        question.setQuestionIndex(questionIndex);
        switch (question.getQuestionType()) {
            case "TextQuestion":
                question.setTextMaximum(textMaximum);
                question.setTextMinimum(textMinimum);
                question.setTextMultiline(textMultiline);
                break;
            case "RangeQuestion":
                question.setEndValue(endValue == 0.0f ? 5.0f : stepSize);
                question.setStartValue(startValue);
                question.setStepSize(stepSize == 0.0f ? 1.0f : stepSize);
                question.setBelowMessage(belowMessage == null ? "" : belowMessage);
                question.setAboveMessage(aboveMessage == null ? "" : aboveMessage);
                question.setHasConsistencyRelationship(hasConsistencyRelationship);
                break;
            case "SliderQuestion":
                question.setEndValue(endValue == 0.0f ? 1.0f : stepSize);
                question.setStartValue(startValue);
                question.setStepSize(stepSize == 0.0f ? 0.01f : stepSize);
                question.setBelowMessage(belowMessage == null ? "" : belowMessage);
                question.setAboveMessage(aboveMessage == null ? "" : aboveMessage);
                question.setHideValues(hideValues);
                break;
            case "ChoiceQuestion":
                question.setAnswerPossibilities(answerPossibilities);
                question.setNumberOfPossibleAnswers(numberOfPossibleAnswers);
                question.setUserAnswers(userAnswers);
                question.setHasConsistencyRelationship(hasConsistencyRelationship);
                break;
            case "SortQuestion":
                question.setAnswerPossibilities(answerPossibilities);
                break;
            default: break;
        }
        return question;
    }

}
