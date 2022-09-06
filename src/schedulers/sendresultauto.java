package schedulers;

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
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.Properties;

@SuppressWarnings("Duplicates")
public class sendresultauto {
    public static void main(String[] args) {
        Connection conn = null;
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = null;

        try {
            conn = getConnection();
            String t_id = "";
            String o_id = "";
            String email = "";
            String path = "";
            String filename = "";
            String OrderNum = "";
            int MRN = 0;
            String Database = "roverlab";
            int emailStatus = 0;

            Query = " SELECT c.id,b.id, IFNULL(a.email,''), IFNULL(c.Reportpath,''),IFNULL(c.filename,''), "
                    + "b.Status,a.MRN,b.OrderNum "
                    + " FROM  " + Database + ".PatientReg a\r\n" +
                    " LEFT JOIN  " + Database + ".TestOrder b ON a.ID=b.PatRegIdx \r\n" +
                    " LEFT JOIN  " + Database + ".Tests c ON b.ID=c.OrderId \r\n" +
                    " where  b.status IN (6,7) and b.email=0 AND c.TestIdx != 4 AND a.email NOT IN('tabish.hafeez@fam-llc.com','amustufa@fam-llc.com') AND b.OrderDate > '2022-06-01 00:00:00' limit 50";
            System.out.println(Query);
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
                t_id = hrset.getString(1);
                o_id = hrset.getString(2);
                email = hrset.getString(3);
                path = hrset.getString(4);
                filename = hrset.getString(5);
                MRN = hrset.getInt(7);
                OrderNum = hrset.getString(8);
                if (email.length() != 0) {
                    emailStatus = sendEmail(t_id, o_id, email, path, filename, conn, OrderNum, MRN);
                    updateEmailRecord(conn, o_id, emailStatus, Database);
                }
            }
            hrset.close();
            hstmt.close();

        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("ERROR IN MAIN --> " + e.getMessage());
            String str = "";
            for (int j = 0; j < e.getStackTrace().length; ++j) {
                str = str + e.getStackTrace()[j] + "<br>";
            }
            System.out.println(str);
        }


    }

    private static String updateEmailRecord(Connection conn, String o_id, int mstatus, String Database) {
        try {
            Statement stmt = null;
            String Query = "";

            Query = "UPDATE " + Database + ".TestOrder SET status=7, email=" + mstatus + ",emailtime=now() WHERE Id = " + o_id;
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();

        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Exception in Updating Email Record " + e.getMessage());
        }

        return null;
    }

    private static int sendEmail(String t_id, String o_id, String email, String path, String filename, Connection conn, String orderNum, int MRN) {
        int emailSent = 0;
        try {
            ResultSet rset = null;
            Statement stmt = null;
            String Query = "";

            String filePath = "https://rovermd.com:4443/md/md.Filedispatcher?p=" + path + "&fn=" + filename;

            URL url = new URL(filePath);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

            InputStream inStream = httpConn.getInputStream();

            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length()).replace("fn=", "");

            String directoryName = "";
            Query = "Select dbname, IFNULL(DirectoryName,'') from oe.clients where Id = 36";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                directoryName = rset.getString(2);
            }
            rset.close();
            stmt.close();

            String outputFilePath = "/sftpdrive/AdmissionBundlePdf/" + directoryName + "/Results/" + fileName.replace("&", "");

            FileOutputStream fout = new FileOutputStream(outputFilePath);

            byte[] buffer = new byte[4096];
            int bytesRead = -1;

            while ((bytesRead = inStream.read(buffer)) != -1) {
                fout.write(buffer, 0, bytesRead);
            }

            inStream.close();
            fout.close();

            emailSent = sendEmailWithAttachment("COVID Test Result", conn, email, outputFilePath, orderNum, MRN);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return emailSent;
    }

    private static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            return DriverManager.getConnection("jdbc:mysql://rovermd-01-master.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=rovermdadmin&password=atyu!ioujy1986&autoReconnect=true");
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    private static int sendEmailWithAttachment(String eSubject, Connection conn, String EmailTo, String filepath, String orderNum, int MRN) {

        CallableStatement cStmt = null;
        ResultSet rset = null;
        String Query = "";
        String emailHtmlFilePath = "/sftpdrive/opt/Htmls/md/EmailFormats/";
        try {
            try {
                String HostName = "";
                String EmailUserId = "";
                String EmailPassword = "";
                String SMTP = "";
                String Port = "";
                String Authentication = "";
                try {
                    Query = "{CALL SP_GET_CredentialsEmail()}";
                    cStmt = conn.prepareCall(Query);
                    rset = cStmt != null ? cStmt.executeQuery() : null;
                    if (rset != null && rset.next()) {
                        HostName = rset.getString(1).trim();
                        EmailUserId = rset.getString(2);
                        EmailPassword = rset.getString(3);
                        SMTP = rset.getString(4);
                        Port = rset.getString(5);
                        Authentication = rset.getString(6);
                    }
                    if (rset != null) {
                        rset.close();
                    }
                    if (cStmt != null) {
                        cStmt.close();
                    }
                } catch (Exception Ex) {
                    Ex.printStackTrace();
                }
                //1) get the session object

                Properties props = new Properties();
                props.put("mail.transport.protocol", SMTP);
                props.put("mail.smtp.host", HostName);
                props.put("mail.smtp.port", Port);
                props.put("mail.smtp.auth", Authentication);
                final String user = EmailUserId;//change accordingly
                final String password = EmailPassword;//change accordingly
                Session session = Session.getDefaultInstance(props,
                        new Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(user, password);
                            }
                        });


                //session.setDebug(true);
                //Sending a HTML file in Email's
                StringWriter writer = new StringWriter();
                IOUtils.copy(new FileInputStream(new File(emailHtmlFilePath + "Results.html")), writer);

                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress("PrimeScope Diagnostic <no-reply@rovermd.com>"));
                // Set To: header field of the header.
                EmailTo = (EmailTo == null ? "tabish.hafeez@fam-llc.com" : EmailTo.equals("") ? "tabish.hafeez@fam-llc.com" : EmailTo);
                System.out.println("EMAIL ADDRESS FROM UH --> " + EmailTo);
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(EmailTo));
                // Set Subject: header field
                message.setSubject("COVID Test Result");
                //Setting the email priority high
                message.addHeader("X-Priority", "1");

                Transport t = session.getTransport("smtp");
                t.connect();

                // attachement
                Multipart multipart = new MimeMultipart();
                BodyPart messageBodyPart = new MimeBodyPart();
                BodyPart attachmentBodyPart = new MimeBodyPart();
                messageBodyPart.setContent(writer.toString().replace("URL$$", "https://app1.rovermd.com:8443/md/md.result?ActionID=GetInput&oid=" + orderNum + "&m=" + MRN + ""), "text/html"); // 5

                multipart.addBodyPart(messageBodyPart);

                System.out.println("SEND EMAIL FILE PATH " + filepath);
                File filename = new File(filepath);

                DataSource source = new FileDataSource(filename);
                attachmentBodyPart.setDataHandler(new DataHandler(source));
                attachmentBodyPart.setFileName(filename.getName());
                multipart.addBodyPart(attachmentBodyPart);
                message.setContent(multipart);

                Transport.send(message);

                System.out.println("Email Sent..");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

}
