package gpse.umfrato.domain.evaluation.filter.filterimpl;

import gpse.umfrato.domain.evaluation.filter.Filter;
import gpse.umfrato.domain.pollresult.PollResult;

import java.text.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateFilter implements Filter {
    private Date start;
    private Date end;
    private final boolean inverted;
    private boolean startEmpty = false;
    private boolean endEmpty = false;
    private boolean valid = true;

public DateFilter(final String startDate, final String endDate, final Boolean inverted) {
    if (!(startEmpty = startDate.isEmpty())) {
        try {
            start = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.GERMAN).parse(startDate);
        } catch (ParseException pe) {
            valid = false;
        }
    }
    if(!(endEmpty = endDate.isEmpty())) {
        try {
            end = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.GERMAN).parse(endDate);
        } catch (ParseException pe) {
            valid = false;
        }
    }
    this.inverted = inverted;
}

    @Override
    public String getFilterType() {
        return "date";
    }

@Override
public List<PollResult> filter(final List<PollResult> input) {

    if (valid) {
        final List<PollResult> filteredList = new ArrayList<>();
        for (final PollResult pr: input) {
            Date date;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN).parse(pr.getLastEditAt());
            } catch (ParseException pe) {
                continue;
            }
            if (((startEmpty || date.compareTo(start) >= 0) && (endEmpty || date.compareTo(end) <= 0)) != inverted) {
                filteredList.add(pr);
            }
        }
        return filteredList;
    } else {
        return input;
    }
}
}
