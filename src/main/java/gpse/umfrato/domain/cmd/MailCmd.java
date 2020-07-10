package gpse.umfrato.domain.cmd;

import lombok.Data;

import java.util.List;

@Data
public class MailCmd {
    private Long pollId;
    private String anonymityStatus;
    private List<String> csvFile;
    private int notificateParticipants;
    private List<String> mailList;
    private String emailSubject;
    private String emailMessage;
}
