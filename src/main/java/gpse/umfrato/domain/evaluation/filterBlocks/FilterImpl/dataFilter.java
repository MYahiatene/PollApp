package gpse.umfrato.domain.evaluation.filterBlocks.FilterImpl;

import gpse.umfrato.domain.pollresult.PollResult;

import java.util.List;

public class dataFilter implements filter {
@Override public List<PollResult> filter(final List<PollResult> input) {
    return input;
}

@Override public List<Double> compute(final List<PollResult> input) {
    return null;
}
}
