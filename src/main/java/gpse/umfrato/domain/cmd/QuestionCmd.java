package gpse.umfrato.domain.cmd;

import lombok.Data;

import java.util.List;

@Data
public class QuestionCmd {

    private String pollId;

    private String questionMessage;

    private List<String> answerPossibilities;

    private String questionType;

    private int endValue;

    private int startValue;

    private int stepSize;

    private String aboveMessage;

    private String belowMessage;

    private Boolean hideValues;

    private int questionIndex;

}
