package Testing;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class EmailTesting {

    public static void main(String[] args) {
        int i = SendEmail("test", "test", "test", "tabish.hafeez@fam-llc.com", "8");
        System.out.println("Email Val " + i);
    }

    public static int SendEmail(String eSection, String eSubject, String eBody, String Email, String facilityIndex) {
        String SMTP_HOST_NAME = "smtp.ionos.com";
        String Port = "587";
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.port", Port);
        props.put("mail.smtp.auth", "true");
        try {

            Authenticator auth = new SMTPAuthenticator();
            Session mailSession = Session.getInstance(props, auth);
            mailSession.setDebug(true);
            Transport transport = mailSession.getTransport();

            MimeMessage message = new MimeMessage(mailSession);
            message.setContent(eBody, "text/html");
            message.setSubject(eSubject);
            message.setFrom(new InternetAddress("App Rover <tabish.hafeez@fam-llc.com>"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(Email));
            message.setSentDate(new Date());
            transport.connect();
            transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
            transport.close();
            System.out.println("1");
        } catch (Exception var18) {
            System.out.println("Error while Generating Email!!!");
            System.out.println(var18.getMessage());
            return 0;
        }

        return 1;
    }

    private static class SMTPAuthenticator extends Authenticator {
        private SMTPAuthenticator() {
        }

        public PasswordAuthentication getPasswordAuthentication() {
            String SMTP_HOST_NAME = "smtp.ionos.com";
            String SMTP_AUTH_USER = "alert@rovermd.com";
            String SMTP_AUTH_PWD = "Ale$Rtr0VeMd(Com";

            String password = SMTP_AUTH_PWD;
            return new PasswordAuthentication(SMTP_AUTH_USER, password);
        }
    }
}
