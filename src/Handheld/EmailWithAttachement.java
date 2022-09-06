package Handheld;

import org.apache.commons.io.IOUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Properties;

@SuppressWarnings("Duplicates")
public class EmailWithAttachement {
    public static void main(String[] args) {

        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int transFound = -1;
        Connection conn = null;
        try {
            String connect_string = "jdbc:mysql://rovermd-01-master.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=rovermdadmin&password=atyu!ioujy1986&characterEncoding=utf8";
            //        String DRIVER = "com.mysql.jdbc.Driver";
            String DRIVER = "com.mysql.cj.jdbc.Driver";
            try {
                Class.forName(DRIVER).newInstance();
                conn = DriverManager.getConnection(connect_string);
            } catch (Exception var11) {
                conn = null;
                System.out.println("Exception excp conn: " + var11.getMessage());
                return;
            }

            Query = "SELECT COUNT(*) FROM .TransactionCredentials WHERE UserPassword = ' ' ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                transFound = rset.getInt(1);
            }
            rset.close();
            stmt.close();


        } catch (Exception Ex) {
           SendEmailWithAttachmentNN("Main Section", "Error In Transaction report", "Message Body", Ex, "Transactionreport", "TransactionPassword");

        }
    }

    private static void SendEmailWithAttachmentNN(String eSection, String eSubject, String eBody, Exception exp, String ClassName, String FuncName) {
        String Email1 = "tabish.hafeez@fam-llc.com";//change accordingly
        final String user = "alert@rovermd.com";//change accordingly
        final String password = "Ale$Rtr0VeMd(Com";//change accordingly
        try {
            //1) get the session object
            String Port = "587";
            String SMTP_HOST_NAME = "smtp.ionos.com";
            Properties props = new Properties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", SMTP_HOST_NAME);
            props.put("mail.smtp.port", Port);
            props.put("mail.smtp.auth", "true");

            Session session = Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(user, password);
                        }
                    });


            //Sending a HTML file in Email's
            StringWriter writer = new StringWriter();
            IOUtils.copy(new FileInputStream(new File("F://EmailFormatter.html")), writer);

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress("App Rover <no-reply@rovermd.com>"));
            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(Email1));
            // Set Subject: header field
            message.setSubject("My Subject");

            Transport t = session.getTransport("smtp");
            t.connect();

            // attachement
            Multipart multipart = new MimeMultipart();
            BodyPart messageBodyPart = new MimeBodyPart();
            BodyPart attachmentBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(writer.toString(), "text/html"); // 5

            multipart.addBodyPart(messageBodyPart);

            /************* MY PART *************/
            String FileName = "";
            String str = "";
            //FileName = Services.GetEmailLogsPath(servletContext);
            FileName = "F://Test.log";
            FileWriter fr = new FileWriter(FileName, true);
            for (int i = 0; i < exp.getStackTrace().length; ++i) {
                str = String.valueOf(str) + exp.getStackTrace()[i] + "<br>";
            }
            fr.write(new Date().toString() + "^" + "Class" + "^" + "Finc" + "^" + exp.getMessage() + str + "\r\n");
            PrintWriter pr = new PrintWriter(fr, true);
            fr.write("\r\n");

            /**********************************/

            //String fName = "F://Test.log";//change accordingly
            // file path
            File filename = new File(String.valueOf(FileName));

            DataSource source = new FileDataSource(filename);
            attachmentBodyPart.setDataHandler(new DataHandler(source));

            attachmentBodyPart.setFileName(filename.getName());

            multipart.addBodyPart(attachmentBodyPart);

            message.setContent(multipart);

            Transport.send(message);

            System.out.println("Mail completed");
            fr.flush();
            fr.close();
            pr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnectionlocal() {
        try {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Connection connection = DriverManager.getConnection("jdbc:mysql://54.167.174.84/oe?user=abdf890092&password=980293339jjjj");

            //Connection connection = DriverManager.getConnection("jdbc:mysql://54.80.137.178/oe?user=abdf890092&password=980293339jjjj");
            return connection;
        } catch (Exception e) {
            System.out.println("PL " + e.getMessage());
            return null;
        }
    }
}
