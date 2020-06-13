package gpse.umfrato.domain.evaluation.filterBlocks.FilterImpl;


import gpse.umfrato.domain.pollresult.PollResult;

import java.util.List;

public interface filter {

    List<PollResult> filter(List<PollResult> input);

    List<Double> compute(List<PollResult> input);

}
