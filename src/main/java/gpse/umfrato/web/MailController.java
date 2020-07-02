package gpse.umfrato.web;

import gpse.umfrato.domain.cmd.CsvCmd;
import gpse.umfrato.domain.mail.MailConfig;
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

@RequestMapping("/api")
@RestController
@CrossOrigin
public class MailController {


    /* default */ static final int DEFAULT_PORT = 8080;

    private JavaMailSender mailSender;

    @Autowired
    public MailController(final JavaMailSender javaMailSender) {
        this.mailSender = javaMailSender;
    }

    @PostMapping(value = "/sendEmail")
    public String sendEmail(final @RequestBody CsvCmd csvCmd) {

        try {
            //List<String> mails = csvCmd.getMailList();

            final SimpleMailMessage message = new SimpleMailMessage();
            //System.out.println("---------SIZE:" + mails.size());
            //for (String mail : mails) {
                final UUID uuid = UUID.randomUUID();
                final String urlUuid = "/" + uuid.toString();
                final URL invitationLink = new URL("http", "localhost", DEFAULT_PORT, urlUuid);

                //message.setFrom("gpseteam5.1@gmail.com");
                //System.out.println(mail);
                message.setTo("test@gmail.com");
                message.setSubject("Einladung zur Umfrage - Umfrato Reply");
                message.setText("Hi, I'm a test mail! \nYour link is \n\n" + invitationLink + "\n\nThank you!");

                this.mailSender.send(message);
            //}

            return "Email sent.";

        } catch (MailException e) {

            return "Email sending failed.";

        } catch (MalformedURLException e) {

            return "Creating unique value failed.";

        }

    }

}
