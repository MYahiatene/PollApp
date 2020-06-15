package gpse.umfrato.domain.evaluation.filterblocks.filterimpl;


import gpse.umfrato.domain.pollresult.PollResult;

import java.util.List;

public interface Filter {

    List<PollResult> filter(List<PollResult> input);

    List<Double> compute(List<PollResult> input);

}
