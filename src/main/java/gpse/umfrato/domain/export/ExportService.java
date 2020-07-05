package gpse.umfrato.domain.export;

import com.fasterxml.jackson.core.JsonProcessingException;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.pollresult.PollResult;

import java.util.List;

public interface ExportService {
    String toCSVManual(Poll poll);

    String toJSON(Poll result) throws JsonProcessingException;

    Poll fromJSONToPoll(String json);

    String toJSON(List<PollResult> result) throws JsonProcessingException;

    List<PollResult> fromJSONToResult(String json);

    String createExportJSON(Poll poll, List<PollResult> result);

}
