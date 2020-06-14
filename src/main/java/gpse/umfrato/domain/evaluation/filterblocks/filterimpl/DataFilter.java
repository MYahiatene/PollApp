package gpse.umfrato.domain.evaluation.filterblocks.filterimpl;

import gpse.umfrato.domain.pollresult.PollResult;

import java.util.List;

public class DataFilter implements Filter {
@Override public List<PollResult> filter(final List<PollResult> input) {
    return input;
}

@Override public List<Double> compute(final List<PollResult> input) {
    return null;
}
}
