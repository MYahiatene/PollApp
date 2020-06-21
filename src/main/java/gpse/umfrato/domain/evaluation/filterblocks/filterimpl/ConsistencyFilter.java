package gpse.umfrato.domain.evaluation.filterblocks.filterimpl;

import gpse.umfrato.domain.ConsistencyQuestion.ConsistencyQuestion;
import gpse.umfrato.domain.ConsistencyQuestion.ConsistencyQuestionService;
import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.pollresult.PollResult;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class ConsistencyFilter implements Filter {

    private final int minNumberOfSuccesses;
    private final List<ConsistencyQuestion> consistencyQuestionList;

    public ConsistencyFilter(final List<ConsistencyQuestion> consistencyQuestionList, final int minNumberOfSuccesses)
    {
        this.consistencyQuestionList = consistencyQuestionList;
        this.minNumberOfSuccesses = minNumberOfSuccesses;
    }

    @Override public List<PollResult> filter(List<PollResult> input) {
        final List<PollResult> filteredList = new ArrayList<>();
        for (final PollResult pr: input) {
            int successNr = 0;
            for (ConsistencyQuestion cq:consistencyQuestionList)
            {
                int match1 = 0;
                for (Answer a:pr.getAnswerList())
                {
                    if (a.getQuestionId().equals(cq.getQuestion1Id()))
                    {
                        for (String answer:a.getGivenAnswerList())
                        {
                            if(cq.getAnswer1Indices().contains(answer))
                            {
                                match1++;
                            }
                        }
                        match1 -= a.getGivenAnswerList().size();
                        break;
                    }
                }
                int match2 = 0;
                for (Answer a:pr.getAnswerList())
                {
                    if (a.getQuestionId().equals(cq.getQuestion2Id()))
                    {
                        for (String answer:a.getGivenAnswerList())
                        {
                            if(cq.getAnswer2Indices().contains(answer))
                            {
                                match2++;
                            }
                        }
                        match2 -= a.getGivenAnswerList().size();
                        break;
                    }
                }
                if(match1 == 0 && match2 == 0)
                {
                    successNr++;
                }
            }
            if (successNr >= minNumberOfSuccesses) {
                filteredList.add(pr);
            }
        }
        return filteredList;
    }

    @Override public List<Double> compute(List<PollResult> input) {
        return null;
    }
}
