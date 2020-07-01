package gpse.umfrato.web;

import gpse.umfrato.domain.cmd.CsvCmd;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

@RequestMapping(value = "/api", method = RequestMethod.GET)
@RestController
@CrossOrigin
public class MailController {

    /* default */ static final int DEFAULT_PORT = 8080;

    @Autowired
    private JavaMailSender mailSender;

    @ResponseBody
    @PostMapping(value = "/sendEmail", produces = MediaType.APPLICATION_JSON_VALUE)
    public String sendEmail(final @RequestBody CsvCmd csvCmd) {

        try {
            List<String> mails = csvCmd.getMailList();

            final SimpleMailMessage message = new SimpleMailMessage();
            System.out.println("---------SIZE:" + mails.size());
            /*for (String mail : mails) {
                final UUID uuid = UUID.randomUUID();
                final String urlUuid = "/" + uuid.toString();
                final URL invitationLink = new URL("http", "localhost", DEFAULT_PORT, urlUuid);

                //message.setFrom("gpseteam5.1@gmail.com");
                System.out.println(mail);
                message.setTo(mail);
                message.setSubject("Einladung zur Umfrage - Umfrato Reply");
                message.setText("Hi, I'm a test mail! \nYour link is \n\n" + invitationLink + "\n\nThank you!");

                this.mailSender.send(message);
            }*/

            return "Email sent.";

        } catch (MailException e) {

            return "Email sending failed.";

        } /*catch (MalformedURLException e) {

            return "Creating unique value failed.";

        }*/

    }

}
