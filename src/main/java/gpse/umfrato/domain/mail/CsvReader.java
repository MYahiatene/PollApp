package gpse.umfrato.domain.mail;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CsvReader {

    public List<String> readEmailsFromList(final List<String> mailArrayList) {

        final List<String> emails = mailArrayList;

        return emails;
    }

}
