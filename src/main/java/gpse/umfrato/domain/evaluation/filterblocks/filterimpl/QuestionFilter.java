package gpse.umfrato.domain.evaluation.filterblocks.filterimpl;

import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.pollresult.PollResult;
import lombok.AllArgsConstructor;

import java.util.*;

@AllArgsConstructor
public class QuestionFilter implements Filter {

private final Long targetPollId;

private final Long targetQuestionId;

private final List<String> targetAnswerPossibilities;

private final boolean inverted;

private final boolean absoluteMatch;

@Override public List<PollResult> filter(final List<PollResult> input) {
    final List<PollResult> filteredList = new ArrayList<>();
    for (final PollResult pr: input) {
        boolean match = false;
        if (pr.getPollId().equals(targetPollId)) {
            for (final Answer a: pr.getAnswerList()) {
                if (a.getQuestionId().equals(targetQuestionId)) {
                    if (absoluteMatch) {
                        match = true;
                        for (final String ga: a.getGivenAnswerList()) {
                            if (!targetAnswerPossibilities.contains(ga)) {
                                match = false;
                                break;
                            }
                        }
                    } else {
                        for (final String ga: a.getGivenAnswerList()) {
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
        if (match != inverted) {
            filteredList.add(pr);
        }
    }
    return filteredList;
}

@Override public List<Double> compute(final List<PollResult> input) {
    return null;
}
}
