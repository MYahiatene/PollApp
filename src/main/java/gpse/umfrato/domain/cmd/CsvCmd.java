package gpse.umfrato.domain.cmd;

import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Data
public class CsvCmd {
    private List<String> csvFile;
    private List<String> mailList;
    private String emailMessage;
}
