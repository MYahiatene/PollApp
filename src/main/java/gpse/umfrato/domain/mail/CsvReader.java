package gpse.umfrato.domain.mail;

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

public class CsvReader {

    public List<String> readEmailsFromCsv(String fileName) {
        Logger logger = null;

        List<String> emails = new ArrayList<>();

        try {
            File file = new File(fileName);
            Scanner inputStream = new Scanner(file);
            while (inputStream.hasNext()) {
                String data = inputStream.next();
                emails.add(data);
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return emails;
    }

}
