package gpse.umfrato.domain.evaluation.session;

import gpse.umfrato.domain.cmd.FilterCmd;
import gpse.umfrato.domain.cmd.SessionCmd;
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

    @Autowired
    public SessionServiceImpl(final SessionRepository sessionRepository, final FilterService filterService) {
        this.sessionRepository = sessionRepository;
        this.filterService = filterService;
    }

    @Override
    public Session createSession(final SessionCmd sessionCmd) {
        final Session session = new Session();
        cmdToSession(sessionCmd, filterService.saveFilterList(sessionCmd.getFilterList()), session);
        sessionRepository.save(session);
        return session;
    }

    @Override
    public void deleteSession(final long sessionId) {
        final Session session = sessionRepository.findById(sessionId).orElseThrow(EntityNotFoundException ::new);
        sessionRepository.deleteById(session.getSessionId());
    }

    @Override public void deleteAllSessions(long pollId) {
        sessionRepository.deleteAllByPollId(pollId);
    }

    @Override
    public List<Session> getAllSessions(final long pollId) {
        return sessionRepository.findSessionsByPollId(pollId);
    }

    @Override
    public Session getSession(final long sessionId) {
        return sessionRepository.findById(sessionId).orElseThrow(EntityNotFoundException ::new);
    }

    @Override
    public Session editSession(final SessionCmd sessionCmd) {
        final Session session = sessionRepository.getOne(sessionCmd.getSessionId());
        cmdToSession(sessionCmd, filterService.updateFilterList(sessionCmd.getFilterList(), session.getFilterList()),
            session);
        sessionRepository.save(session);
        return session;
    }

    @Override public List<FilterCmd> getFilters(final Long sessionId) {
        try {
            final List<FilterData> filterList = sessionRepository.findById(sessionId).
                orElseThrow(EntityNotFoundException :: new).getFilterList();
            final List<FilterCmd> filters = new ArrayList<>();
            for (final FilterData fd:filterList) {
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
