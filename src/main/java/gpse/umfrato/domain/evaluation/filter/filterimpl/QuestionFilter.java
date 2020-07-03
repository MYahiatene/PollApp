package gpse.umfrato.domain.evaluation.filter.filterimpl;

import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.evaluation.filter.Filter;
import gpse.umfrato.domain.pollresult.PollResult;
import lombok.AllArgsConstructor;

import java.util.*;

@AllArgsConstructor
public class QuestionFilter implements Filter {

private final Long targetPollId;

private final Long targetQuestionId;

private final List<String> targetAnswerPossibilities;

private final boolean inverted;

private final boolean workWithIntervall;

private final boolean requireAbsoluteMatch;

    @Override
    public String getFilterType() {
        return "questionAnswer";
    }

@Override public List<PollResult> filter(final List<PollResult> input) {
    final List<PollResult> filteredList = new ArrayList<>();
    double lowerBorder = 0.0;
    double upperBorder = 1.0;
    if(workWithIntervall)
    {
        lowerBorder = Double.parseDouble(targetAnswerPossibilities.get(0));
        upperBorder = Double.parseDouble(targetAnswerPossibilities.get(1));
    }
    for (final PollResult pr: input) {
        boolean match = false;
        if (pr.getPollId().equals(targetPollId)) {
            for (final Answer a: pr.getAnswerList()) {
                if (a.getQuestionId().equals(targetQuestionId)) {
                    if (workWithIntervall) {
                        match = true;
                        for (final String ga: a.getGivenAnswerList()) {
                            double value;
                            try {
                                value = Double.parseDouble(ga);
                            }catch (NumberFormatException e) {
                                match = false;
                                break;
                            }
                            if (value < lowerBorder || upperBorder < value) {
                                match = false;
                                break;
                            }
                        }
                    } else if (requireAbsoluteMatch) {
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
}
