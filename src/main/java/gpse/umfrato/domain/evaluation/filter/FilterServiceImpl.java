package gpse.umfrato.domain.evaluation.filter;

import gpse.umfrato.domain.cmd.FilterCmd;
import gpse.umfrato.domain.evaluation.filter.filterimpl.ConsistencyFilter;
import gpse.umfrato.domain.evaluation.filter.filterimpl.DateFilter;
import gpse.umfrato.domain.evaluation.filter.filterimpl.QuestionFilter;
import gpse.umfrato.domain.evaluation.filter.filterimpl.UserFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FilterServiceImpl implements FilterService {

    private final FilterRepository filterRepository;

    @Autowired
    public FilterServiceImpl(FilterRepository filterRepository) {
        this.filterRepository = filterRepository;
    }

    @Override
    public List<FilterData> saveFitlerList(List<FilterCmd> filterCmds) {
        List<FilterData> filter = new ArrayList<>();
        cmdsToFilters(filterCmds,filter);
        return filterRepository.saveAll(filter);
    }

    @Override
    public List<FilterData> updateFitlerList(List<FilterCmd> filterCmds, final List<FilterData> filter) {
        cmdsToFilters(filterCmds,filter);
        return filterRepository.saveAll(filter);
    }

    @Override
    public void deleteFilterData(final Long filterId) {
        filterRepository.deleteById(filterId);
    }

    private void cmdsToFilters(List<FilterCmd> filterCmds, List<FilterData> filter) {
        for(FilterCmd cmd: filterCmds)
        {
            FilterData fd = new FilterData();
            fd.setFilterType(cmd.getFilterType());
            fd.setInvertFilter(cmd.getInvertFilter());
            switch (fd.getFilterType())
            {
                case "dataFilter":
                    fd.setBasePollId(cmd.getBasePollId());
                    fd.setBaseQuestionIds(cmd.getBaseQuestionIds());
                    fd.setTimeDiagram(cmd.getTimeDiagram());
                    break;
                case "questionAnswer":
                    fd.setTargetQuestionId(cmd.getTargetQuestionId());
                    fd.setTargetAnswerPossibilities(cmd.getTargetAnswerPossibilities());
                    fd.setIsSlider(cmd.getIsSlider());
                    break;
                case "consistency":
                    fd.setMinSuccesses(cmd.getMinSuccesses());
                    break;
                case "date":
                    fd.setStartDate(cmd.getStartDate());
                    fd.setEndDate(cmd.getEndDate());
                    break;
                case "user":
                    fd.setUserNames(cmd.getUserNames());
                    break;
                default:
                    break;
            }
            filter.add(fd);
        }
    }
}
