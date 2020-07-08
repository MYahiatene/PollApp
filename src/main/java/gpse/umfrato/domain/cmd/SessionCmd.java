package gpse.umfrato.domain.cmd;

import lombok.Data;

import java.util.List;

@Data
public class SessionCmd {
    private Long sessionId = -1L;

    private Long pollId;

    private String sessionTitle;

    private List<FilterCmd> filterList;

    private List<String> diagramFormat;

    private List<String> diagramOptions;

    private String lastUsername;
}
