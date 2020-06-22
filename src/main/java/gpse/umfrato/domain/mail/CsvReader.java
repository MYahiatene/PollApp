package gpse.umfrato.domain.mail;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {

    public List<String> readEmailsFromCsv(String fileName) {
        List<String> emails = new ArrayList<>();
        BufferedReader br = null;
        String line = "";
        String csvSplitBy = ",";

        try {

            br = new BufferedReader(new FileReader(fileName));

            while ((line = br.readLine()) != null) {
                String[] text =  line.split(csvSplitBy);
                emails.add(text[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return emails;
    }

}
