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
                List<String> givenAnswers1 = new ArrayList<>();
                List<String> givenAnswers2 = new ArrayList<>();
                for (Answer a: pr.getAnswerList()) {
                    if (cq.getQuestion1Id().equals(a.getQuestionId())) {
                        givenAnswers1 = a.getGivenAnswerList();
                    }
                    if (cq.getQuestion2Id().equals(a.getQuestionId())) {
                        givenAnswers2 = a.getGivenAnswerList();
                    }
                }
                int matched = 0;
                boolean matchA = false;
                for (String s: givenAnswers1) {
                    if (cq.getQuestion1Slider()) {
                        double value = Double.parseDouble(s);
                        double min = Double.parseDouble(cq.getAnswer1Indices().get(0));
                        double max = Double.parseDouble(cq.getAnswer1Indices().get(1));
                        if (min <= value && value <= max) {
                            matchA = true;
                            break;
                        }
                    } else {
                        if (cq.getAnswer1Indices().contains(s)) {
                            matchA = true;
                            break;
                        }
                    }
                }
                if (matchA) {
                    matched++;
                }
                boolean matchB = false;
                for (String s: givenAnswers2) {
                    if (cq.getQuestion2Slider()) {
                        double value = Double.parseDouble(s);
                        double min = Double.parseDouble(cq.getAnswer2Indices().get(0));
                        double max = Double.parseDouble(cq.getAnswer2Indices().get(1));
                        if (min <= value && value <= max) {
                            matchB = true;
                            break;
                        }
                    } else {
                        if (cq.getAnswer2Indices().contains(s)) {
                            matchB = true;
                            break;
                        }
                    }
                }
                if (matchB) {
                    matched++;
                }
                if (matched != 1) {
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
