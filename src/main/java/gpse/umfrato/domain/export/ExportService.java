package gpse.umfrato.domain.export;

import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.pollresult.PollResult;

import java.util.List;

public interface ExportService {
    String toCSVManual(Poll poll);

    String toJSON(Poll result) throws Exception;

    Poll fromJSONToPoll(String json);

    String toJSON(List<PollResult> result) throws Exception;

    List<PollResult> fromJSONToResult(String json);

    String createExportJSON(Poll poll, List<PollResult> result);

}
