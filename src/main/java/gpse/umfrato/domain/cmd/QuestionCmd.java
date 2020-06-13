package gpse.umfrato.domain.cmd;

import gpse.umfrato.domain.poll.Poll;
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

    private Long questionId;

    private Long categoryId;

    private String questionMessage;

    private List<String> answerPossibilities;

    private String questionType;

    private boolean userAnswers;

    private int numberOfPossibleAnswers;

    public Question getQuestion() {
        final Question question = new Question(questionMessage, answerPossibilities, questionType);
        switch(question.getQuestionType()){
            case "TextQuestion":
                question.setTextMaximum(textMaximum);
                question.setTextMinimum(textMinimum);
                question.setTextMultiline(true);
                break;
            case "RangeQuestion":
            case "SliderQuestion":
                question.setEndValue(endValue);
                question.setStartValue(startValue);
                question.setStepSize(stepSize);
                question.setBelowMessage(belowMessage == null ? "" : belowMessage);
                question.setAboveMessage(aboveMessage == null ? "" : aboveMessage);
                break;
            case "ChoiceQuestion":
                question.setAnswerPossibilities(answerPossibilities);
                question.setNumberOfPossibleAnswers(numberOfPossibleAnswers);
                break;
            case "SortQuestion":
                question.setAnswerPossibilities(answerPossibilities);
                break;
            default: break;
        }
        return question;
    }

}
