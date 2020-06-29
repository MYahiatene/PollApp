package gpse.umfrato.domain.evaluation.filter.filterimpl;

import gpse.umfrato.domain.evaluation.filter.Filter;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.pollresult.PollResult;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class UserFilter implements Filter {
    private final List<String> userList;
    private final boolean inverted;

    @Override public List<PollResult> filter(final List<PollResult> input) {
        final List<PollResult> filteredList = new ArrayList<>();
        for(final PollResult pr:input)
        {
            if(userList.contains(pr.getPollTaker()) != inverted)
            {
                filteredList.add(pr);
            }
        }
        return filteredList;
    }
}
