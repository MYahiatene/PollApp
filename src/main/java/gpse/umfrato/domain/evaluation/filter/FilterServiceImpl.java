package gpse.umfrato.domain.evaluation.filter;

import gpse.umfrato.domain.cmd.FilterCmd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FilterServiceImpl implements FilterService {

    private final FilterRepository filterRepository;

    @Autowired
    public FilterServiceImpl(final FilterRepository filterRepository) {
        this.filterRepository = filterRepository;
    }

    @Override
    public List<FilterData> saveFilterList(final List<FilterCmd> filterCmds) {
        final List<FilterData> filter = new ArrayList<>();
        cmdsToFilters(filterCmds, filter);
        return filterRepository.saveAll(filter);
    }

    @Override
    public List<FilterData> updateFilterList(final List<FilterCmd> filterCmds, final List<FilterData> filter) {
        cmdsToFilters(filterCmds, filter);
        return filterRepository.saveAll(filter);
    }

    @Override
    public void deleteFilterData(final Long filterId) {
        filterRepository.deleteById(filterId);
    }

    private void cmdsToFilters(final List<FilterCmd> filterCmds, final List<FilterData> filter) {
        for (final FilterCmd cmd: filterCmds) {
            final FilterData fd = new FilterData();
            fd.setFilterType(cmd.getFilterType());
            fd.setInvertFilter(cmd.getInvertFilter());
            switch (fd.getFilterType()) {
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

    @Override
    public FilterCmd filterToCmd(final FilterData fd) {
        final FilterCmd cmd = new FilterCmd();
        cmd.setFilterType(fd.getFilterType());
        cmd.setInvertFilter(fd.getInvertFilter());
        switch (cmd.getFilterType()) {
            case "dataFilter":
                cmd.setBasePollId(fd.getBasePollId());
                cmd.setBaseQuestionIds(fd.getBaseQuestionIds());
                cmd.setTimeDiagram(fd.getTimeDiagram());
                return cmd;
            case "questionAnswer":
                cmd.setTargetQuestionId(fd.getTargetQuestionId());
                cmd.setTargetAnswerPossibilities(fd.getTargetAnswerPossibilities());
                cmd.setIsSlider(fd.getIsSlider());
                return cmd;
            case "consistency":
                cmd.setMinSuccesses(fd.getMinSuccesses());
                return cmd;
            case "date":
                cmd.setStartDate(fd.getStartDate());
                cmd.setEndDate(fd.getEndDate());
                return cmd;
            case "user":
                cmd.setUserNames(fd.getUserNames());
                return cmd;
        }
        return cmd;
    }
}
