package gpse.umfrato.domain.evaluation.filter;

import gpse.umfrato.domain.cmd.FilterCmd;

import java.util.List;

public interface FilterService {
    List<FilterData> saveFitlerList(final List<FilterCmd> filterCmds);

    List<FilterData> updateFitlerList(List<FilterCmd> filterCmds, final List<FilterData> filter);

    void deleteFilterData(final Long filterId);


}
