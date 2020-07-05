package gpse.umfrato.domain.evaluation.filter;

import gpse.umfrato.domain.cmd.FilterCmd;

import java.util.List;

public interface FilterService {
    List<FilterData> saveFilterList(final List<FilterCmd> filterCmds);

    List<FilterData> updateFilterList(List<FilterCmd> filterCmds, final List<FilterData> filter);

    void deleteFilterData(final Long filterId);

    FilterCmd filterToCmd(final FilterData fd);
}
