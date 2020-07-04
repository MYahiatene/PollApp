package gpse.umfrato.domain.evaluation.filter.filterimpl;

import gpse.umfrato.domain.evaluation.filter.Filter;
import gpse.umfrato.domain.pollresult.PollResult;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class OrFilter implements Filter {
    private final List<Filter> filterList;

    @Override
    public String getFilterType() {
        return "or";
    }

    @Override public List<PollResult> filter(List<PollResult> input) {
        final List<PollResult> filteredList = new ArrayList<>();
        for(PollResult pr:input) {
            for (Filter f: filterList) {
                if (f.filter(Collections.singletonList(pr)).size() == 1) {
                    filteredList.add(pr);
                    break;
                }
            }
        }
        return filteredList;
    }
}
