package md;

import org.apache.commons.io.IOUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.util.Properties;

@SuppressWarnings("Duplicates")
public class EmailServices {

    public static int SendEmail(String eSection, String eSubject, String eBody) {
        // Recipient's email ID needs to be mentioned.
        String Email1 = "tabish.hafeez@fam-llc.com";
        //String Email2 = "m.abid@fam-llc.com";
        //String Email3 = "m.abid@fam-llc.com";
        String SMTP_HOST_NAME = "smtp.ionos.com";
        String Port = "587";
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.port", Port);
        props.put("mail.smtp.auth", "true");
        try {

            //Sending a HTML file in Email's
            StringWriter writer = new StringWriter();
//            IOUtils.copy(new FileInputStream(new File("F://EmailFormatter.html") , "the text".getBytes(), StandardOpenOption.APPEND), writer);
            //IOUtils.copy(new FileInputStream(new File("/sftpdrive/opt/Htmls/md/EmailFormats/EmailFormatter.html")), writer);
            IOUtils.copy(new FileInputStream(new File("F://EmailFormatter.html")), writer);


            Authenticator auth = new SMTPAuthenticator();
            Session mailSession = Session.getInstance(props, auth);
            //mailSession.setDebug(true);
            Transport transport = mailSession.getTransport();
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(mailSession);
            //message.setContent(Body, "text/html");
            // Set From: header field of the header.
            message.setFrom(new InternetAddress("App Rover <no-reply@rovermd.com>"));
            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(Email1));
            // Set Subject: header field
            message.setSubject(eSubject);


            BodyPart messageBodyPart1 = new MimeBodyPart();
            messageBodyPart1.setText(writer.toString());

            //4) create new MimeBodyPart object and set DataHandler object to this object
            MimeBodyPart messageBodyPart2 = new MimeBodyPart();
            String filename = "F://Test.log";//change accordingly
            DataSource source = new FileDataSource(filename);
            messageBodyPart2.setDataHandler(new DataHandler(source));
            messageBodyPart2.setFileName(filename);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart1);
            multipart.addBodyPart(messageBodyPart2);

            //6) set the multiplart object to the message object
            message.setContent(multipart, "text/html");
            // Send the actual HTML message, as big as you like
//            message.setContent(writer.toString(), "text/html");
            //Attaching the Date in email
            //message.setSentDate(new Date());

            transport.connect();
            transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
            transport.close();
            System.out.println("message sent....");

/*            message.setDataHandler(new DataHandler(
                    new FileDataSource("F://EmailFormatter.html", "text/html")));*/

            //message.addRecipient(Message.RecipientType.TO, new InternetAddress(Email2));
/*            Address[][] cc = new Address[][]{InternetAddress.parse("abc@abc.com"),
                    InternetAddress.parse("abc@def.com"),
                    InternetAddress.parse("ghi@abc.com")};
            message.addRecipients(Message.RecipientType.CC, cc);*/

        } catch (Exception var18) {
            System.out.println("Error while Generating Email!!!");
            System.out.println(var18.getMessage());
        }

        return 1;
    }

    public static void main(String[] args) throws Exception {
        SendEmail("Section", "My Subject", "Test Body");
    }

    public void init() {

    }

    private static class SMTPAuthenticator extends Authenticator {
        private SMTPAuthenticator() {
        }

        public PasswordAuthentication getPasswordAuthentication() {
            String SMTP_HOST_NAME = "smtp.ionos.com";
            String SMTP_AUTH_USER = "alert@rovermd.com";
            String SMTP_AUTH_PWD = "Ale$Rtr0VeMd(Com";

            String username = SMTP_AUTH_USER;
            String password = SMTP_AUTH_PWD;
            return new PasswordAuthentication(username, password);
        }
    }
}
