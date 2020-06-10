package gpse.umfrato.domain.Evaluation.FilterBlocks.FilterImpl;

import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.pollresult.PollResult;
import lombok.AllArgsConstructor;
import net.bytebuddy.description.method.ParameterList;

import java.util.*;

@AllArgsConstructor
public class QuestionFilter implements Filter {

private final Long targetPollId;

private final Long targetQuestionId;

private final List<String> targetAnswerPossibilities;

private final boolean absoluteMatch;

@Override public List<PollResult> filter(List<PollResult> input) {
    int index = 0;
    List<PollResult> filteredList = new ArrayList<>();
    for(PollResult pr: input)
    {
        boolean match = false;
        if(pr.getPollId().equals(targetPollId)) {
            for (Answer a: pr.getAnswerList()) {
                if (a.getQuestionId().equals(targetQuestionId)) {
                    if (absoluteMatch) {
                        match = true;
                        for (String ga: a.getGivenAnswerList()) {
                            if (!targetAnswerPossibilities.contains(ga)) {
                                match = false;
                            }
                        }
                    } else {
                        for (String ga: a.getGivenAnswerList()) {
                            if (targetAnswerPossibilities.contains(ga)) {
                                match = true;
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
        if(match)
        {
            filteredList.add(pr);
        }
        index++;
    }
    return filteredList;
}

@Override public List<Double> compute(List<PollResult> input) {
    return null;
}
}
