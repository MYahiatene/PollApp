package gpse.umfrato.mail;

import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

        private static final String EMAIL_USERNAME = "test@gmail.com";
        private static final String EMAIL_PASSWORD = "password";

        private static final int EMAIL_PORT = 587;
        private static final String TRUE = "true";

        public JavaMailSender getJavaMailSender() {
            final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("smtp.gmail.com");
            mailSender.setPort(EMAIL_PORT);

            mailSender.setUsername(EMAIL_USERNAME);
            mailSender.setPassword(EMAIL_PASSWORD);

            final Properties properties = mailSender.getJavaMailProperties();
            properties.put("mail.transport.protocol", "smtp");
            properties.put("mail.smtp.auth", TRUE);
            properties.put("mail.smtp.starttls.enable", TRUE);
            properties.put("mail.debug", TRUE);

            return mailSender;
        }
}
