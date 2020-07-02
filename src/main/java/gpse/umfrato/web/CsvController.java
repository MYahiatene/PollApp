package gpse.umfrato.web;

import gpse.umfrato.domain.cmd.MailCmd;
import gpse.umfrato.domain.mail.CsvReader;
import gpse.umfrato.domain.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@RestController
@CrossOrigin
public class CsvController {

    private final MailService mailService;
    private final CsvReader csvReader;

    @Autowired
    public CsvController(final CsvReader csvReader, final MailService mailService) {
        this.mailService = mailService;
        this.csvReader = csvReader;
    }

    @PostMapping(value = "/sendCsv")
    //@PreAuthorize("hasAuthority('Admin')")
    public String getEmailsFromCsv(final @RequestBody MailCmd mailCmd) {
        try {
            List<String> file = mailCmd.getCsvFile();
            mailService.setMailAdresses(csvReader.readEmailsFromList(file));
            return "Creating mail list finished!";
        } catch (BadRequestException e) {
            return "Creating mail list failed!";
        }
    }

    @GetMapping("/emailList")
    public List<String> getEmailList() {
        return mailService.getMailAdresses();
    }
}
