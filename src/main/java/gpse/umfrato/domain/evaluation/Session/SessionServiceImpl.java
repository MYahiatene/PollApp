package gpse.umfrato.domain.evaluation.Session;

import gpse.umfrato.domain.cmd.FilterCmd;
import gpse.umfrato.domain.cmd.SessionCmd;
import gpse.umfrato.domain.consistencyquestion.ConsistencyQuestionService;
import gpse.umfrato.domain.evaluation.filter.FilterData;
import gpse.umfrato.domain.evaluation.filter.FilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final FilterService filterService;
    private final ConsistencyQuestionService consistencyQuestionService;

    @Autowired
    public SessionServiceImpl(final SessionRepository sessionRepository, final FilterService filterService, final ConsistencyQuestionService consistencyQuestionService) {
        this.sessionRepository = sessionRepository;
        this.filterService = filterService;
        this.consistencyQuestionService = consistencyQuestionService;
    }

    @Override
    public Session createSession(SessionCmd sessionCmd) {
        Session session = new Session();
        cmdToSession(sessionCmd,filterService.saveFilterList(sessionCmd.getFilterList()),session);
        sessionRepository.save(session);
        return session;
    }

    @Override
    public void deleteSession(long sessionId) {
        Session s = sessionRepository.findById(sessionId).orElseThrow(EntityNotFoundException ::new);
        sessionRepository.deleteById(s.getSessionId());
    }

    @Override
    public List<Session> getAllSessions(long pollId) {
        return sessionRepository.findSessionsByPollId(pollId);
    }

    @Override
    public Session getSession(long sessionId) {
        return sessionRepository.findById(sessionId).orElseThrow(EntityNotFoundException ::new);
    }

    @Override
    public Session editSession(SessionCmd sessionCmd) {
        Session session = sessionRepository.getOne(sessionCmd.getSessionId());
        cmdToSession(sessionCmd,filterService.updateFilterList(sessionCmd.getFilterList(),session.getFilterList()),session);
        sessionRepository.save(session);
        return session;
    }

    @Override public List<FilterCmd> getFilters(Long sessionId) {
        try {
            List<FilterData> filterList = sessionRepository.findById(sessionId).orElseThrow(EntityNotFoundException :: new).getFilterList();
            List<FilterCmd> filters = new ArrayList<>();
            for(FilterData fd:filterList)
            {
                filters.add(filterService.filterToCmd(fd));
            }
            return filters;
        } catch (EntityNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void cmdToSession(final SessionCmd sessionCmd, final List<FilterData> filterList, final Session session) {
        session.setPollId(sessionCmd.getPollId());
        session.setSessionTitle(sessionCmd.getSessionTitle());
        session.setFilterList(filterList);
        session.setDiagramFormat(sessionCmd.getDiagramFormat());
        session.setDiagramOptions(sessionCmd.getDiagramOptions());
        session.setLastUsername(sessionCmd.getLastUsername());
        session.setLastEdited(new Date());
    }
}
