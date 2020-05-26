package gpse.umfrato.domain.Evaluation.FilterBlocks.FilterImpl;


import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.pollresult.PollResult;

import java.util.List;

public interface Filter {

    List<PollResult> filter(List<PollResult> input);

    List<Double> compute(List<PollResult> input);

}
