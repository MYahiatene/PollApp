package gpse.umfrato.domain.Evaluation.FilterBlocks.FilterImpl;

import gpse.umfrato.domain.pollresult.PollResult;

import java.util.List;

public class QuestionFilter implements Filter {
    String targetQuestion;
    List<String> selectedAnswers;
    boolean inverted = false;

    public QuestionFilter(String jsonInput){



    }


@Override public List<PollResult> filter(List<PollResult> input) {
    return null;
}

@Override public List<Double> compute(List<PollResult> input) {
    return null;
}
}
