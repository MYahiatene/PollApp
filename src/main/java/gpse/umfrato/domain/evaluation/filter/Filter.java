package gpse.umfrato.domain.evaluation.filter;


import gpse.umfrato.domain.pollresult.PollResult;

import java.util.List;

public interface Filter {

    String getFilterType();

    List<PollResult> filter(List<PollResult> input);

}
