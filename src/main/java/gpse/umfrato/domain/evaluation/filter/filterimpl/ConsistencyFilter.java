package gpse.umfrato.domain.evaluation.filter.filterimpl;

import gpse.umfrato.domain.consistencyquestion.ConsistencyQuestion;
import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.evaluation.filter.Filter;
import gpse.umfrato.domain.pollresult.PollResult;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ConsistencyFilter implements Filter {

    private final List<ConsistencyQuestion> consistencyQuestionList;
    private final int minNumberOfSuccesses;

    @Override
    public String getFilterType() {
        return "consistency";
    }

    @Override
    public List<PollResult> filter(final List<PollResult> input) {
        final List<PollResult> filteredList = new ArrayList<>();
        for (final PollResult pr: input) {
            int successNr = 0;
            for (final ConsistencyQuestion cq: consistencyQuestionList) {
                int match = 0;
                int noMatch = 0;
                for (final Answer a: pr.getAnswerList()) {
                    if (a.getQuestionId().equals(cq.getQuestion1Id())) {
                        for (final String answer: a.getGivenAnswerList()) {
                            if (cq.getQuestion1Slider()) {
                                if (Double.parseDouble(cq.getAnswer1Indices().get(0)) <= Double.parseDouble(answer) && Double.parseDouble(cq.getAnswer1Indices().get(1)) >= Double.parseDouble(answer)) {
                                    match++;
                                } else {
                                    noMatch++;
                                }
                            } else {
                                if (cq.getAnswer1Indices().contains(answer)) {
                                    match++;
                                } else {
                                    noMatch++;
                                }
                            }
                        }
                        break;
                    }
                }
                if (match != 0 && noMatch != 0) {
                    continue;
                }
                for (final Answer a: pr.getAnswerList()) {
                    if (a.getQuestionId().equals(cq.getQuestion2Id())) {
                        for (final String answer: a.getGivenAnswerList()) {
                            if (cq.getQuestion2Slider()) {
                                if (Double.parseDouble(cq.getAnswer2Indices().get(0)) <= Double.parseDouble(answer) && Double.parseDouble(cq.getAnswer2Indices().get(1)) >= Double.parseDouble(answer)) {
                                    match++;
                                } else {
                                    noMatch++;
                                }
                            } else {
                                if (cq.getAnswer2Indices().contains(answer)) {
                                    match++;
                                } else {
                                    noMatch++;
                                }
                            }
                        }
                        break;
                    }
                }
                if (match == 0 || noMatch == 0) {
                    successNr++;
                }
            }
            if (successNr >= minNumberOfSuccesses) {
                filteredList.add(pr);
            }
        }
        return filteredList;
    }
}
