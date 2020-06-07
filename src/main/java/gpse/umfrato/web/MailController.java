package gpse.umfrato.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

@Controller
public class MailController {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    public MailController() {
        this.mailSender = mailSender;
    }

    @ResponseBody
    @RequestMapping("/sendEmail")
    public String sendEmail() {

        try {

            final SimpleMailMessage message = new SimpleMailMessage();

            UUID uuid = UUID.randomUUID();
            String urlUuid = "/" + uuid.toString();
            URL invitationLink = new URL("http", "localhost", 8080, urlUuid);

            message.setTo("gpseteam5.1@gmail.com");
            message.setSubject("Einladung zur Umfrage - Umfrato Reply");
            message.setText("Hi, I'm a test mail! \nYour link is \n\n" + invitationLink + "\n\nThank you!");

            this.mailSender.send(message);

            return "Email sent.";

        } catch (MailException e) {

            return "Email sending failed.";

        } catch (MalformedURLException e) {

            return "Creating unique value failed.";

        }

    }

}
