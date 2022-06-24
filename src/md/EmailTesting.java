package md;

import Parsehtm.Parsehtm;
import org.apache.commons.io.IOUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Date;
import java.util.Properties;

@SuppressWarnings("Duplicates")
public class EmailTesting extends HttpServlet {
    private Connection conn = null;

    private static void SendEmailWithAttachmentNN(String eSection, String eSubject, String eBody, Exception exp, String ClassName, String FuncName, ServletContext servletContext) {
        String Email1 = "tabish.hafeez@fam-llc.com";//change accordingly
        final String user = "alert@rovermd.com";//change accordingly
        final String password = "Ale$Rtr0VeMd(Com";//change accordingly
        String FilePath = Services.GetEmailLogsPath(servletContext);
        String emailHtmlFilePath = Services.GetEmailFilePath(servletContext);
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
            //IOUtils.copy(new FileInputStream(new File("F://EmailFormatter.html")), writer);
            IOUtils.copy(new FileInputStream(new File(emailHtmlFilePath + "EmailFormatter.html")), writer);
//            IOUtils.copy(new FileInputStream(new File("/sftpdrive/opt/Htmls/md/EmailFormats/EmailFormatter.html")), writer);

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
            try {
                FileName = FilePath + "myLogs.log";
                //FileName = "/sftpdrive/opt/Htmls/md/logs/EmailLogs/myLogs.log";
                final FileWriter fr = new FileWriter(FileName, true);
                String str = "";
                for (int i = 0; i < exp.getStackTrace().length; ++i) {
                    str = String.valueOf(str) + exp.getStackTrace()[i] + "<br>";
                }
                fr.write(new Date().toString() + "^" + ClassName + "^" + FuncName + "^" + exp.getMessage() + str + "\r\n");
                final PrintWriter pr = new PrintWriter(fr, true);
                exp.printStackTrace(pr);
                fr.write("\r\n");
                fr.flush();
                fr.close();
                pr.close();
            } catch (Exception ex) {
            }

            /**********************************/

            String fName = FilePath + "myLogs.log";//change accordingly
            //String fName = "/sftpdrive/opt/Htmls/md/logs/EmailLogs/myLogs.log";//change accordingly
            // file path
            File filename = new File(fName);

            DataSource source = new FileDataSource(filename);
            attachmentBodyPart.setDataHandler(new DataHandler(source));

            attachmentBodyPart.setFileName(filename.getName());

            multipart.addBodyPart(attachmentBodyPart);

            message.setContent(multipart);

            Transport.send(message);

            //Delete the file
            Files.deleteIfExists(Paths.get(fName));
            System.out.println("Mail completed..");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        requestHandling(request, response);
    }

    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        requestHandling(request, response);
    }

    public void requestHandling(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = new PrintWriter(response.getOutputStream());
        try {

            ServletContext context;
            context = this.getServletContext();
            conn = Services.getMysqlConn(context);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }

            String ServiceRequests = request.getParameter("ServiceRequests").trim();
            switch (ServiceRequests) {
/*                case "sendEmail":
                    sendEmail(request, conn, context, out);
                    break;*/
                case "SendEmail":
                    SendEmail(request, context, conn, out);
                default:
                    out.println("1");
            }

        } catch (Exception e) {
            out.println("Error in DB Connection " + e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            out.flush();
            out.close();
        }
    }

    private void sendEmail(HttpServletRequest req, Connection conn, ServletContext servletContext, PrintWriter out) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        int transFound = -1;
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
            out.println("Sending Email...");
            SendEmailWithAttachmentNN("Main Section", "Error In Transaction report", "Message Body", Ex, "Transactionreport", "TransactionPassword", servletContext);

        }
    }

    public int SendEmail(HttpServletRequest request, ServletContext context, Connection conn, PrintWriter out) {
        String eSection = request.getParameter("Section");
        String eSubject = request.getParameter("Subject");
        String eBody = request.getParameter("Body");
        String sendTo = request.getParameter("sendTo");
        String Body = "";
        //String Email1 = "alert@rovermd.com";
        String SMTP_HOST_NAME = "smtp.ionos.com";
        String Port = "587";
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.port", Port);
        props.put("mail.smtp.auth", "true");
        try {

            Body = "<center><b><font size= '4'>" + eSection + "</font></b></center>";
            Body = Body + "<center><font size= '2'><br><br>";
            Body = Body + "<table width=100% cellpading=0 cellspacing=0 bgcolor=\"#FFFFFF\">";
            Body = Body + "<tr><td width=100% class=\"fif\" bgcolor=\"#FFFFFF\"><font color=\"#000000\">";
            Body = Body + eBody;
            Body = Body + "</font></td></tr>";
            Body = Body + "</table><br>";

            Authenticator auth = new SMTPAuthenticator();
            Session mailSession = Session.getInstance(props, auth);
            //mailSession.setDebug(true);
            Transport transport = mailSession.getTransport();

            MimeMessage message = new MimeMessage(mailSession);
            message.setContent(Body, "text/html");
            message.setSubject(eSubject);
            message.setFrom(new InternetAddress("App Rover <no-reply@rovermd.com>"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(sendTo));
            message.setSentDate(new Date());
            transport.connect();
            transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
            transport.close();
            System.out.println("1");
        } catch (Exception var18) {
            System.out.println("Error while Generating Email!!!");
            System.out.println(var18.getMessage());
        }

        return 1;
    }

    private class SMTPAuthenticator extends Authenticator {
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
