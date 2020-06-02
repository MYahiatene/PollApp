package gpse.umfrato.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

            message.setTo("gpseteam5.1@gmail.com");
            message.setSubject("This is a test mail");
            message.setText("Hi, I'm a test mail!");

            this.mailSender.send(message);

            return "Email sent.";

        } catch (MailException e) {

            return "Email sending failed.";

        }

    }

}
