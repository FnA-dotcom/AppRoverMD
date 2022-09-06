package md;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Properties;

public class dailyvisitalert {


    static String MSG1="";
    String MSG2="";


    public static void main(String[] args) {
// TODO Auto-generated method stub
        Statement stmt = null;
        ResultSet rset = null;
        Statement hstmt1 = null;
        ResultSet hrset1 = null;
        String Query = "";
        String Query1 = "";
//        String email="alisaadbaig@gmail.com";
//        String email="m.mehmood@fam-llc.com";
        String email="mr.mouhid@gmail.com";
        int i = 1;
        String Stage="0";
        String Alert="";
        String content="";
        Connection conn = null;

        try
        {

            System.out.println("YES");
            StringBuilder contentBuilder = new StringBuilder();
            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                public void checkServerTrusted(X509Certificate[] certs, String authType) { }

            } };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) { return true; }
            };
            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            /* End of the fix*/


//############

            String URL = "https://app1.rovermd.com:8443/md/md.EmailDashboardCounts?ActionID=GetInput";

            Stage = "1";
            String XML1 = "";
            Stage = "4";

            URL url = new URL(URL);
    /* char[] buffer = new char[10240];
     int bytes_read = 0;
     bytes_read = XML1.getBytes().length;*/

            URLConnection urlc = url.openConnection();
            urlc.setRequestProperty("Content-Type", "text/html");
            urlc.setDoOutput(true);
            urlc.setDoInput(true);
      /* PrintWriter pw = new PrintWriter(urlc.getOutputStream());
       pw.write(XML1, 0, bytes_read);
       pw.close();*/
            BufferedReader in = new BufferedReader(new InputStreamReader(urlc.getInputStream()));

            String html1 = "";
            String inputLine;
            contentBuilder.setLength(0);
            while ((inputLine = in.readLine()) != null)
            {

                contentBuilder.append(inputLine);



            }


            System.out.println(contentBuilder.toString());

            if(email.contains(","))
            {
                System.out.println("YES");
                String[] Emailsplit=email.split(",");
                System.out.println(Emailsplit.length);

                for(int i2=0;i2<Emailsplit.length;i2++)
                {
                    System.out.println(Emailsplit[i2]);
                    email=Emailsplit[0];

                }

            }

   /* contentBuilder.append("<style> table, th, td { border: 1px solid black;}</style> ");
    contentBuilder.append(" <body> ");

    contentBuilder.append(" <table cellpadding='0' cellspacing='0' width='640' align='center' >     ");
    contentBuilder.append(" <tr>         ");
    contentBuilder.append(" <td>             ");
    contentBuilder.append(" <table cellpadding='0' cellspacing='0' width='318' align='left' >                 ");
    contentBuilder.append(" <tr>                     ");
    contentBuilder.append(" <td>This is where the logo goes.</td>                 ");
    contentBuilder.append(" </tr>             ");
    contentBuilder.append(" </table>             ");
    contentBuilder.append(" <table cellpadding='0' cellspacing='0' width='318' align='left' >                 ");
    contentBuilder.append(" <tr>                     ");
    contentBuilder.append(" <td>This is where the image goes.</td>                 ");
    contentBuilder.append(" </tr>             ");
    contentBuilder.append(" </table>         ");
    contentBuilder.append(" </td>     ");
    contentBuilder.append(" </tr>     ");
    contentBuilder.append(" <tr>         ");
    contentBuilder.append(" <td>Here is some more content in a new table row.</td>     ");
    contentBuilder.append(" </tr> ");
    contentBuilder.append(" </table> ");
    contentBuilder.append(" </body> ");
*/

            String Email=SendEmail(contentBuilder.toString(),email);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Stage 0.1"+Query);
            System.out.println(Stage);
        }

    }



    private static String SendEmail(String MSGA,String email){



        String ToEmail;
        String ToEmail1;
        String ToEmail2;
        String EmailSubject;
        String HTMLText="Hello";
        String reply="";
        String Alert="";

 /*   email : alert@rovermd.com
    pwd : Ale$Rtr0VeMd(Com
    port : 587
    HostName : smtp.ionos.com
    */


        final String CONTA_PADRAO = "alert@rovermd.com";
        final String SENHA_CONTA_PADRAO = "Ale$Rtr0VeMd(Com";
        final String domain="smtp.ionos.com";
        String from ="alert@rovermd.com";


        Properties config = new Properties();
        config.put("mail.smtp.auth", "true");
        // config.put("mail.smtp.starttls.enable", "true");
        config.put("mail.smtp.host", domain);
        config.put("mail.transport.protocol", "smtp");
        config.put("mail.smtp.port", "587");
        //  config.put("mail.smtp.ssl.trust", "email-smtp.us-east-2.amazonaws.com");
        String to = "alisaadbaig@gmail.com";


        Session session = Session.getInstance(config, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(CONTA_PADRAO, SENHA_CONTA_PADRAO);
            }

        });


        try {
            MSG1=MSGA;

            HTMLText=MSGA;
    /* HTMLText=MSGA.replace("Customer_Name",Customername);
    HTMLText=HTMLText.replace("YANC",cid);
    HTMLText=HTMLText.replace("YAD",amount);
    HTMLText=HTMLText.replace("YDD",extdate);
    HTMLText=HTMLText.replace("urlaa","https://ourenergyllc.com/oe/pay?uid="+link);*/
            // HTMLText=HTMLText.replace("AAAPAY",Button);
            // HTMLText=HTMLText.replace("STYLLLLL",tmp);

            final Message message = new MimeMessage(session);
            session.setDebug(false);
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
            // message.setRecipient(Message.RecipientType.BCC, new InternetAddress("malik@ourenergyllc.com"));
//            message.setRecipient(Message.RecipientType.BCC, new InternetAddress("alisaadbaig@gmail.com"));
            message.setFrom(new InternetAddress(from));
            // message.setSubject(Alert);
            message.setSubject("Last Day Report");
            //   message.setText(messageContent);
            message.setContent(HTMLText, "text/html");
            message.setSentDate(new Date());
            Transport.send(message);
            // session.
            // System.out.println(HTMLText);

        } catch (Exception ex) {
            System.out.println("Email -------"+ex.getMessage());
            return ex.getMessage();
        }

        System.out.println(Alert);
        return "1";


    }




}
