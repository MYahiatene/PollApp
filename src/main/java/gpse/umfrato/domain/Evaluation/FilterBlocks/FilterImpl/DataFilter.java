package gpse.umfrato.domain.Evaluation.FilterBlocks.FilterImpl;

import gpse.umfrato.domain.pollresult.PollResult;

import java.util.List;

public class DataFilter implements Filter {
@Override public List<PollResult> filter(List<PollResult> input) {
    return input;
}

@Override public List<Double> compute(List<PollResult> input) {
    return null;
}
}
