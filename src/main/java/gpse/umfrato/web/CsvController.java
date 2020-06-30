package gpse.umfrato.web;

import gpse.umfrato.domain.cmd.CsvCmd;
import gpse.umfrato.domain.mail.CsvReader;
import gpse.umfrato.domain.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@RequestMapping(value = "/api", method = RequestMethod.GET)
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

    @PostMapping(value = "/sendCsv", produces = MediaType.APPLICATION_JSON_VALUE)
    //@PreAuthorize("hasAuthority('Admin')")
    public String getEmailsFromCsv(final @RequestBody CsvCmd csvCmd) {
        try {
            File file = csvCmd.getCsvFile();
            mailService.setMailAdresses(csvReader.readEmailsFromCsvFile(file));
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
