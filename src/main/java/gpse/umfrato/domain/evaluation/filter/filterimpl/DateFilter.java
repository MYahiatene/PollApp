package gpse.umfrato.domain.evaluation.filter.filterimpl;

import gpse.umfrato.domain.evaluation.filter.Filter;
import gpse.umfrato.domain.pollresult.PollResult;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;

public class DateFilter implements Filter {
    private ZonedDateTime start;
    private ZonedDateTime end;
    private final boolean inverted;
    private boolean startEmpty = false;
    private boolean endEmpty = false;
    private boolean valid = true;

public DateFilter(final String startDate, final String endDate, final Boolean inverted) {
    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.GERMAN);
    if (!(startEmpty = startDate.isEmpty())) {
        try {
            start = ZonedDateTime.parse(startDate, df);
        } catch (DateTimeParseException pe) {
            valid = false;
        }
    }
    if(!(endEmpty = endDate.isEmpty())) {
        try {
            end = ZonedDateTime.parse(startDate, df);
        } catch (DateTimeParseException pe) {
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
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.GERMAN);
        final List<PollResult> filteredList = new ArrayList<>();
        for (final PollResult pr: input) {
            ZonedDateTime date;
            try {
                date = ZonedDateTime.parse(pr.getLastEditAt(), df);
            } catch (DateTimeParseException pe) {
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
