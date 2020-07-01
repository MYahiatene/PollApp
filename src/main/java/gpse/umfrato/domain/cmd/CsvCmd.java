package gpse.umfrato.domain.cmd;

import lombok.Data;

import java.io.File;
import java.util.List;

@Data
public class CsvCmd {
    private File csvFile;
    private List<String> mailList;
}
