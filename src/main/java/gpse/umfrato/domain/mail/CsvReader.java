package gpse.umfrato.domain.mail;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class CsvReader {

    public List<String> readEmailsFromCsvPath(String fileName) {

        List<String> emails = new ArrayList<>();

        try {
            File file = new File(fileName);
            Scanner inputStream = new Scanner(file);
            while (inputStream.hasNext()) {
                emails.add(inputStream.next());
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return emails;
    }

    public List<String> readEmailsFromList(List<String> mailArrayList) {

        List<String> emails = mailArrayList;

        return emails;
    }

}
