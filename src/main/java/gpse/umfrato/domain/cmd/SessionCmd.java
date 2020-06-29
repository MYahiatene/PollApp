package gpse.umfrato.domain.cmd;

import gpse.umfrato.domain.evaluation.filter.FilterData;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SessionCmd {
    private Long sessionId = -1L;

    private Long pollId;

    private String sessionTitle;

    private List<FilterCmd> filterList;

    private List<String> diagramColors;

    private String lastUsername;
}
