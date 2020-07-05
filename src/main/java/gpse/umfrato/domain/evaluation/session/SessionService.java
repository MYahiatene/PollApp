package gpse.umfrato.domain.evaluation.session;

import gpse.umfrato.domain.cmd.FilterCmd;
import gpse.umfrato.domain.cmd.SessionCmd;

import java.util.List;

public interface SessionService {
    Session createSession(SessionCmd sessionCmd);

    void deleteSession(final long sessionId);

    List<Session> getAllSessions(final long pollId);

    Session getSession(final long sessionId);

    Session editSession(final SessionCmd sessionCmd);

    List<FilterCmd> getFilters(final Long sessionId);
}
