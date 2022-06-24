//
// Decompiled by Procyon v0.5.36
//

package md;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

@SuppressWarnings("Duplicates")
public class Services {
    private static String GetExceptionFilePath(final ServletContext context) {
        return context.getInitParameter("log_path");
    }

    private static String GetMobileExceptionFilePath(final ServletContext context) {
        return context.getInitParameter("mobile_log_path");
    }

    public static String GetHtmlPath(final ServletContext context) {
        return context.getInitParameter("html_path");
    }

    public static String GetEmailLogsPath(final ServletContext context) {
        return context.getInitParameter("email_log_path");
    }

    public static String GetEmailFilePath(ServletContext context) {
        return context.getInitParameter("email_file_path");
    }

    public static boolean CheckDates(final String FromDate, final String ToDate, final Connection conn, final int data) {
        return true;
    }

    public static Connection GetConnection(final ServletContext context, final int db) {
        try {
            final String mysql_server = context.getInitParameter("mysql_server");
            String mysql_dbuser = "";
            String mysqlusr = context.getInitParameter("mysqlusr");
            String mysqlpwd = context.getInitParameter("mysqlpwd");
            String mysqldb = context.getInitParameter("mysqldb");
            if (db == 1) {
                mysqldb = context.getInitParameter("mysql_dbuser1");
            } else if (db == 2) {
                mysql_dbuser = context.getInitParameter("mysql_dbuser2");
            } else if (db == 3) {
                mysql_dbuser = context.getInitParameter("mysql_dbuser3");
            }
            //This was activated...
//            Class.forName("com.mysql.jdbc.Driver").newInstance();
//            Class.forName("org.mariadb.jdbc.Driver").newInstance();
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            return DriverManager.getConnection("jdbc:mysql://" + mysql_server + "/" + mysqldb + "?user=" + mysqlusr + "&password=" + mysqlpwd + "&characterEncoding=latin1");
        } catch (Exception _ex) {
            final String a = _ex.getMessage().toString();
            System.out.println(_ex.getMessage());
            return null;
        }
    }

    public static String ConnString(final ServletContext context, final int db) {
        try {
            final String mysql_server = context.getInitParameter("mysql_server");
            String mysql_dbuser = "";
            final String mysqlusr = context.getInitParameter("mysqlusr");
            final String mysqlpwd = context.getInitParameter("mysqlpwd");
            final String mysqldb = context.getInitParameter("mysqldb");
            if (db == 1) {
                mysql_dbuser = context.getInitParameter("mysql_dbuser1");
            } else if (db == 2) {
                mysql_dbuser = context.getInitParameter("mysql_dbuser2");
            } else if (db == 3) {
                mysql_dbuser = context.getInitParameter("mysql_dbuser3");
            }
            final String A = "jdbc:mysql://" + mysql_server + "/" + mysqldb + "?user=" + mysqlusr + "&password=" + mysqlpwd + "";
            return A;
        } catch (Exception _ex) {
            final String a = _ex.getMessage().toString();
            return null;
        }
    }

    public static String GetCookie(final String CookieToSearch, final HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (int coky = 0; coky < cookies.length; ++coky) {
            final String cName = cookies[coky].getName();
            final String cValue = cookies[coky].getValue();
            if (cName.equals(CookieToSearch)) {
                return cValue;
            }
        }
        return null;
    }

    public static boolean GetCalendar(final StringBuffer day, final StringBuffer month, final StringBuffer year) {
        final Date dt = new Date();
        final int d = dt.getDate();
        final int m = dt.getMonth() + 1;
        final int y = dt.getYear() + 1900;
        int i = 0;
        final String[] Months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        if (day == null || month == null || year == null) {
            return false;
        }
        for (i = 1; i <= 31; ++i) {
            if (i == d) {
                day.append("<option value=" + i + " selected>" + i + "</option>");
            } else {
                day.append("<option value=" + i + ">" + i + "</option>");
            }
        }
        for (i = 1; i <= 12; ++i) {
            if (i == m) {
                month.append("<option value=" + i + " selected>" + Months[i - 1] + "</option>");
            } else {
                month.append("<option value=" + i + ">" + Months[i - 1] + "</option>");
            }
        }
        int EndingYear;
        for (EndingYear = dt.getYear() + 1900, i = 2013; i <= EndingYear; ++i) {
            if (i == y) {
                year.append("<option value=" + i + " selected>" + i + "</option>");
            } else {
                year.append("<option value=" + i + ">" + i + "</option>");
            }
        }
        return true;
    }

    public static boolean GetCalendar(final StringBuffer day, final StringBuffer month, final StringBuffer year, final StringBuffer hour, final StringBuffer minutes) {
        final Date dt = new Date();
        final int d = dt.getDate();
        final int m = dt.getMonth() + 1;
        final int y = dt.getYear() + 1900;
        int i = 0;
        final String[] Months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        if (day == null || month == null || year == null) {
            return false;
        }
        for (i = 1; i <= 31; ++i) {
            if (i == d) {
                day.append("<option value=" + i + " selected>" + i + "</option>");
            } else {
                day.append("<option value=" + i + ">" + i + "</option>");
            }
        }
        for (i = 1; i <= 12; ++i) {
            if (i == m) {
                month.append("<option value=" + i + " selected>" + Months[i - 1] + "</option>");
            } else {
                month.append("<option value=" + i + ">" + Months[i - 1] + "</option>");
            }
        }
        int EndingYear;
        for (EndingYear = dt.getYear() + 1900, i = 2013; i <= EndingYear; ++i) {
            if (i == y) {
                year.append("<option value=" + i + " selected>" + i + "</option>");
            } else {
                year.append("<option value=" + i + ">" + i + "</option>");
            }
        }
        for (i = 0; i < 24; ++i) {
            if (i > 9) {
                hour.append("<option value=\"" + i + "\">" + i + "</option>\n");
            } else {
                hour.append("<option value=\"0" + i + "\">0" + i + "</option>\n");
            }
        }
        for (i = 0; i < 60; ++i) {
            if (i > 9) {
                minutes.append("<option value=\"" + i + "\">" + i + "</option>\n");
            } else {
                minutes.append("<option value=\"0" + i + "\">0" + i + "</option>\n");
            }
        }
        return true;
    }

    private static String GetExceptionFileName() {
        try {
            final Date date = GetDate();
            final DecimalFormat decimalformat = new DecimalFormat("#00");
            return decimalformat.format(date.getYear() + 1900) + "_" + decimalformat.format(date.getMonth() + 1) + "_" + decimalformat.format(date.getDate()) + ".log";
        } catch (Exception exception) {
            return "invalid filename " + exception.getMessage();
        }
    }

    private static Date GetDate() {
        try {
            final Date date = new Date();
            return date;
        } catch (Exception _ex) {
            return null;
        }
    }


    private static String GetExceptionFilePath(final HttpServletRequest request) {
        return "/opt/Htmls/logs/";
    }

    public static Connection getMysqlConn(ServletContext context) {
        try {
            String mysql_server = context.getInitParameter("mysql_server");
            String mysql_dbuser = "oe";
            String mysqlusr = context.getInitParameter("mysqlusr");
            String mysqlpwd = context.getInitParameter("mysqlpwd");
            Connection mysqlconn = null;
            //Connection connection = DriverManager.getConnection("jdbc:mysql://"+mysql_server+"/"+mysql_dbuser+"?user="+mysqlusr+"&password="+mysqlpwd+"");
//            Class.forName("com.mysql.jdbc.Driver").newInstance();
            //Class.forName("org.mariadb.jdbc.Driver");
            //return DriverManager.getConnection("jdbc:mysql://127.0.0.1/oe?user=abc1234open&password=abc!@#$1234");
            //System.out.println("Abid COnn"+"jdbc:mysql://"+mysql_server+"/"+mysql_dbuser+"?user="+mysqlusr+"&password="+mysqlpwd+"");
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            return DriverManager.getConnection("jdbc:mysql://" + mysql_server + "/" + mysql_dbuser + "?user=" + mysqlusr + "&password=" + mysqlpwd + "");
//            final Connection mysqlconn = null;
//            Class.forName("com.mysql.jdbc.Driver").newInstance();
//            return DriverManager.getConnection("jdbc:mysql://127.0.0.1/oe_2?user=oe&password=abc1234oe");
        } catch (Exception ex) {
            return null;
        }
    }


    public static void DumException(final String ClassName, final String FuncName, final HttpServletRequest request, final Exception exp) {
        String FileName = "";
        final String UserId = GetCookie("UserId", request);
//        final String UserIP = request.getRemoteAddr() == null ? null : request.getRemoteAddr();
        try {

            StringBuffer requestinfo = new StringBuffer();

            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                requestinfo.append("Header Name - " + headerName + ", Value - " + request.getHeader(headerName) + "\n");
            }

            Enumeration<String> params = request.getParameterNames();
            while (params.hasMoreElements()) {
                String paramName = params.nextElement();
                requestinfo.append("Parameter Name - " + paramName + ", Value - " + request.getParameter(paramName) + "\n");
            }


            FileName = GetExceptionFilePath(request) + GetExceptionFileName();
            final FileWriter fr = new FileWriter(FileName, true);
            fr.write("\r\n ===========================  REQUEST STARTS  ========================  \r\n");

            // Getting servlet request URL
            String url = request.getRequestURL().toString();

            // Getting servlet request query string.
            String queryString = request.getQueryString();

            // Getting request information without the hostname.
            String uri = request.getRequestURI();

            // Below we extract information about the request object path
            // information.
            String scheme = request.getScheme();
            String serverName = request.getServerName();
            int portNumber = request.getServerPort();
            String contextPath = request.getContextPath();
            String servletPath = request.getServletPath();
            String query = request.getQueryString();
//            fr.write("USER IP: " + UserIP + "\n");
            fr.write("Url: " + url + "\n");
            fr.write("Uri: " + uri + "\n");
//            fr.write("IP: " + UserIP + "\n");
            fr.write("Scheme: " + scheme + "\n");
            fr.write("Server Name: " + serverName + "\n");
            fr.write("Port: " + portNumber + "\n");
            fr.write("Context Path: " + contextPath + "\n");
            fr.write("Servlet Path: " + servletPath + "\n");
            fr.write("Query: " + query + "\n");
//            Enumeration enumeration = request.getParameterNames();
//            while(enumeration.hasMoreElements()){
//                String parameterName = (String) enumeration.nextElement();
//                fr.write(parameterName+" "+request.getParameter(parameterName)+" \r\n");
//            }
            fr.write(requestinfo.toString());
            fr.write("\r\n ===========================  REQUEST ENDS  ========================  \r\n");
            fr.write("\r\n ===========================  EXCEPTION STARTS ========================  \r\n");
            fr.write(new Date().toString() + "^" + UserId + "^" + ClassName + "^" + FuncName + "^" + exp.getMessage() + "\r\n");
            final PrintWriter pr = new PrintWriter(fr, true);
            exp.printStackTrace(pr);
            fr.write("\r\n ===========================  EXCEPTION ENDS ========================  \r\n");
            fr.flush();
            fr.close();
            pr.close();
        } catch (Exception ex) {
        }
    }

    public static void DumException(final String ClassName, final String FuncName, final HttpServletRequest request, final Exception exp, final ServletContext servletContext) {
        String FileName = "";
//        final String UserIP = request.getRemoteAddr();
        try {

            StringBuffer requestinfo = new StringBuffer();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                requestinfo.append("Header Name - " + headerName + ", Value - " + request.getHeader(headerName) + "\n");
            }

            Enumeration<String> params = request.getParameterNames();
            while (params.hasMoreElements()) {
                String paramName = params.nextElement();
                requestinfo.append("Parameter Name - " + paramName + ", Value - " + request.getParameter(paramName) + "\n");
            }

            FileName = GetExceptionFilePath(servletContext) + GetExceptionFileName();
            final FileWriter fr = new FileWriter(FileName, true);
            fr.write("\r\n ===========================  REQUEST STARTS  ========================  \r\n");

            // Getting servlet request
            String url = request.getRequestURL().toString();
            String queryString = request.getQueryString();
            String uri = request.getRequestURI();
            String scheme = request.getScheme();
            String serverName = request.getServerName();
            int portNumber = request.getServerPort();
            String contextPath = request.getContextPath();
            String servletPath = request.getServletPath();
            String query = request.getQueryString();

//            fr.write("USER IP: " + UserIP + "\n");
            fr.write("Url: " + url + "\n");
            fr.write("Uri: " + uri + "\n");
//            fr.write("IP: " + UserIP + "\n");
            fr.write("Scheme: " + scheme + "\n");
            fr.write("Server Name: " + serverName + "\n");
            fr.write("Port: " + portNumber + "\n");
            fr.write("Context Path: " + contextPath + "\n");
            fr.write("Servlet Path: " + servletPath + "\n");
            fr.write("Query: " + query + "\n");
//            Enumeration enumeration = request.getParameterNames();
//            while(enumeration.hasMoreElements()){
//                String parameterName = (String) enumeration.nextElement();
//                fr.write(parameterName+" "+request.getParameter(parameterName)+" \r\n");
//            }
            fr.write(requestinfo.toString());
            fr.write("\r\n ===========================  REQUEST ENDS  ========================  \r\n");
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
    }

    //Mobile Logs Dumps
    public static void MobileExceptionDumps(final String ClassName, final String FuncName, final HttpServletRequest request, final Exception exp, final ServletContext servletContext) {
        String FileName = "";
        try {
            FileName = GetMobileExceptionFilePath(servletContext) + GetExceptionFileName();
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
    }

    public static void doLogMethodMessage(ServletContext servletcontext, String Method, String Message, final HttpServletRequest request) {
        try {
            String FileName = GetMobileExceptionFilePath(servletcontext) + GetExceptionFileName();
            java.util.Date dt = new java.util.Date();
            FileWriter fr = new FileWriter(FileName, true);
            fr.write(dt.toString() + " -- " + Method + " -- " + Message + "\r\n");
            fr.write("\r\n");
            fr.flush();
            fr.close();
        } catch (Exception e) {
        }
    }

    public static boolean checkSession(final PrintWriter out, final HttpServletRequest request) {
        if (request.getSession(false) == null) {
            out.println(" <center><table cellpadding=3 cellspacing=2><tr><td bgcolor=\"#FFFFFF\">  <font color=green face=arial><b>Your Session Has Been Expired</b></font>  </td></tr></table>  <p>  <font face=arial size=+1><b><a href=/md/FacilityLogin.html target=_top> Return to login Portal  </a></b></font> <br><font face=arial size=-2>(You will need to sign in again.)</font><br>  </center> ");
            return false;
        }
        return true;
    }

    public static int SendEmail11(String ToEmail, String ToEmail1, String ToEmail2, String EmailSubject, String HTMLText, PrintWriter out, ServletContext servletContext) {
        String to = ToEmail;
        String to1 = ToEmail1;
        String to2 = ToEmail2;

        String SMTP_HOST_NAME = "smtpout.secureserver.net";
        //String SMTP_HOST_NAME = "203.130.0.236";
        final String SMTP_AUTH_USER = "alert@yextel.com";
        final String SMTP_AUTH_PWD = "open_me1234";
        final String CONTA_PADRAO = "alert@yextel.com";
        final String SENHA_CONTA_PADRAO = "open_me1234";


        String from = "alert@yextel.com";

        Session session = Session.getInstance(getEmailProperties(), new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(CONTA_PADRAO, SENHA_CONTA_PADRAO);
            }

        });

        //Session session = Session.getDefaultInstance(props);
        try {
            //session.setDebug(true);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(EmailSubject);
            message.setContent(HTMLText, "text/html");
            Transport.send(message);

            return 1;
        } catch (Exception e) {
            out.println("Unable to send email :" + e.getMessage());
        }
        return -1;
    }

    public static int SendEmail(String ToEmail, String ToEmail1, String ToEmail2, String EmailSubject, String HTMLText, PrintWriter out, ServletContext servletContext) {
        final String CONTA_PADRAO = "alert@yextel.com";
        final String SENHA_CONTA_PADRAO = "open_me1234";
        String from = "alert@yextel.com";
        Properties config = new Properties();
        config.put("mail.smtp.auth", "true");
        config.put("mail.smtp.starttls.enable", "true");
        config.put("mail.smtp.host", "smtp.office365.com");
        config.put("mail.smtp.port", "587");
        String to = ToEmail;
        String to1 = ToEmail1;
        String to2 = ToEmail2;

        Session session = Session.getInstance(config, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(CONTA_PADRAO, SENHA_CONTA_PADRAO);
            }

        });

        try {
            final Message message = new MimeMessage(session);
            session.setDebug(true);
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setFrom(new InternetAddress(from));
            message.setSubject(EmailSubject);
            //   message.setText(messageContent);
            message.setContent(HTMLText, "text/html");
            message.setSentDate(new Date());
            Transport.send(message);
            return 1;
        } catch (final MessagingException ex) {
            return -1;
        }
    }

    private static Properties getEmailProperties() {
        final Properties config = new Properties();
        config.put("mail.smtp.auth", "true");
        config.put("mail.smtp.starttls.enable", "true");
        config.put("mail.smtp.host", "smtp.office365.com");
        config.put("mail.smtp.port", "587");
        return config;
    }

    public void Dologing(String UserId, Connection conn, String UserIP, String ActionID, String MainTask, String SubTask, int ClientId) {
        Statement stmt = null;
        ResultSet rset = null;
        int Id = 0;
        String Query = "";
        String _Unique_id = " ";
        Query = "Select IFNULL(MAX(Id), 0)+1 from oe.SysActivityLogs";
        try {
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Id = rset.getInt(1);
            }
            rset.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println("Error in getting Max Id from SysActivityLogs Table: " + e.getMessage());
        }
        _Unique_id = "LogID-00" + Id;
        try {
            Query = " insert into  oe.SysActivityLogs (UserIP, ActionID, CreatedDate, UserId, " +
                    "UniqueId, MainTask, SubTask, ClientId) values " +
                    "('" + UserIP + "','" + ActionID + "',now(),'" + UserId + "','" + _Unique_id + "'," +
                    "'" + MainTask + "','" + SubTask + "', " + ClientId + ")";
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);
            stmt.close();
        } catch (Exception e) {
            System.out.println("Error in Inserting SysActivityLogs Table: " + e.getMessage() + " ACTION " + ActionID + " MainTask " + MainTask + " SubTask " + SubTask);
        }
    }
}
