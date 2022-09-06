package oe_2;


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
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Properties;

public class Services
{
    public static String GetInetAddr(int ip)
    {
        String IP = "";
        IP = (ip & 0xff) + "." + (ip >> 8 & 0xff) + "." + (ip >> 16 & 0xff) + "." + (ip >> 24 & 0xff);
        return IP.trim();
    }

 
    public static String getInitParams(String str, ServletContext context)
    {
        try
        {
            ServletContext servletcontext = context;
            return servletcontext.getInitParameter(str);
        }
        catch(Exception _ex)
        {
            return null;
        }
    }
	
    public static String GetExceptionFilePath(HttpServletRequest httpservletrequest, ServletContext context)
    {
    	//return "/opt/logs/callcenter/";
    	
    	String path = context.getInitParameter("log_path");
        return path;
    }
     
    public static String GetHtmlPath(ServletContext context)
    { 
    	String path = context.getInitParameter("html_path");
        return path;
    }
    
    public static String GetHtmlPath2(String directory)
    { 
    	return "/opt/log/";
    }
    public static String GetHtmlPath3(String s)
    {
        return "/opt/log/";
    }
    
    public static boolean CheckRights(int screenno,HttpServletRequest request,Connection conn, StringBuffer Response)
    {
    	return true;
    }
    
    public static boolean CheckDates(String FromDate, String ToDate,Connection conn, int data)
    {
    	return true;
    }

    public static Connection GetConnection(ServletContext context, int db)
    {
    	try
        {
    		String mysql_server = context.getInitParameter("mysql_server");
    		String mysql_dbuser = "";
    		String mysqlusr = context.getInitParameter("mysqlusr");
    		String mysqlpwd = context.getInitParameter("mysqlpwd");
    		String mysqldb = context.getInitParameter("mysqldb");
    		
    		if(db == 1) 
    			mysqldb = context.getInitParameter("mysql_dbuser1");	//vtrack_inbound
    		else if(db == 2)
    			mysql_dbuser = context.getInitParameter("mysql_dbuser2");	//vtrackoutbound
    		else if(db == 3)
    			mysql_dbuser = context.getInitParameter("mysql_dbuser3");	//vtrackob_calling
    		
    		
//    		Class.forName("com.mysql.jdbc.Driver").newInstance();
//            Connection connection = DriverManager.getConnection("jdbc:mysql://"+mysql_server+"/"+mysqldb+"?user="+mysqlusr+"&password="+mysqlpwd+"");
            //Connection connection = DriverManager.getConnection("jdbc:mysql://"+mysql_server+"/"+mysqldb+"?user="+mysqlusr+"&password="+mysqlpwd+"");
            Class.forName("org.mariadb.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection("jdbc:mysql://" + mysql_server + "/" + mysqldb + "?user=" + mysqlusr + "&password=" + mysqlpwd + "&characterEncoding=latin1");
            System.out.println(connection);
            return connection;
        }
        catch(Exception _ex)
        {
        	String a=_ex.getMessage().toString();
            return null;
        }
    }
    
    
    public static String ConnString(ServletContext context, int db)
    {
    	try
        {
    		String mysql_server = context.getInitParameter("mysql_server");
    		String mysql_dbuser = "";
    		String mysqlusr = context.getInitParameter("mysqlusr");
    		String mysqlpwd = context.getInitParameter("mysqlpwd");
    		String mysqldb = context.getInitParameter("mysqldb");
    		
    		if(db == 1) 
    			mysql_dbuser = context.getInitParameter("mysql_dbuser1");	//vtrack_inbound
    		else if(db == 2)
    			mysql_dbuser = context.getInitParameter("mysql_dbuser2");	//vtrackoutbound
    		else if(db == 3)
    			mysql_dbuser = context.getInitParameter("mysql_dbuser3");	//vtrackob_calling
    		
    		
    		String A= "jdbc:mysql://"+mysql_server+"/"+mysqldb+"?user="+mysqlusr+"&password="+mysqlpwd+"";
            //Connection connection = DriverManager.getConnection("jdbc:mysql://"+mysql_server+"/"+mysqldb+"?user="+mysqlusr+"&password="+mysqlpwd+"");
            
            return A;
        }
        catch(Exception _ex)
        {
        	String a=_ex.getMessage().toString();
            return null;
        }
    }

    public static String GetCookie(String CookieToSearch, HttpServletRequest request)
    {
        Cookie cookies[] = request.getCookies();
        if(cookies == null)
            return null;
        for(int coky = 0; coky < cookies.length; coky++)
        {
            String cName = cookies[coky].getName();
            String cValue = cookies[coky].getValue();
            if(cName.equals(CookieToSearch))
                return cValue;
        }

        return null;
    }

    public static boolean GetCalendar(StringBuffer day, StringBuffer month, StringBuffer year)
    {
        Date dt = new Date();
        int d = dt.getDate();
        int m = dt.getMonth() + 1;
        int y = dt.getYear() + 1900;
        int i = 0;
        String Months[] = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct",
            "Nov", "Dec"
        };
        if(day == null || month == null || year == null)
            return false;
        for(i = 1; i <= 31; i++)
            if(i == d)
                day.append("<option value=" + i + " selected>" + i + "</option>");
            else
                day.append("<option value=" + i + ">" + i + "</option>");

        for(i = 1; i <= 12; i++)
            if(i == m)
                month.append("<option value=" + i + " selected>" + Months[i - 1] + "</option>");
            else
                month.append("<option value=" + i + ">" + Months[i - 1] + "</option>");

        int EndingYear = dt.getYear() + 1900;
        for(i = 2013; i <= EndingYear; i++)
            if(i == y)
                year.append("<option value=" + i + " selected>" + i + "</option>");
            else
                year.append("<option value=" + i + ">" + i + "</option>");

        return true;
    }

    public static boolean GetCalendar(StringBuffer day, StringBuffer month, StringBuffer year, StringBuffer hour, StringBuffer minutes)
    {
        Date dt = new Date();
        int d = dt.getDate();
        int m = dt.getMonth() + 1;
        int y = dt.getYear() + 1900;
        int i = 0;
        String Months[] = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct",
            "Nov", "Dec"
        };
        if(day == null || month == null || year == null)
            return false;
        for(i = 1; i <= 31; i++)
            if(i == d)
                day.append("<option value=" + i + " selected>" + i + "</option>");
            else
                day.append("<option value=" + i + ">" + i + "</option>");

        for(i = 1; i <= 12; i++)
            if(i == m)
                month.append("<option value=" + i + " selected>" + Months[i - 1] + "</option>");
            else
                month.append("<option value=" + i + ">" + Months[i - 1] + "</option>");

        int EndingYear = dt.getYear() + 1900;
        for(i = 2013; i <= EndingYear; i++)
            if(i == y)
                year.append("<option value=" + i + " selected>" + i + "</option>");
            else
                year.append("<option value=" + i + ">" + i + "</option>");

        for(i = 0; i < 24; i++)
            if(i > 9)
                hour.append("<option value=\"" + i + "\">" + i + "</option>\n");
            else
                hour.append("<option value=\"0" + i + "\">0" + i + "</option>\n");

        for(i = 0; i < 60; i++)
            if(i > 9)
                minutes.append("<option value=\"" + i + "\">" + i + "</option>\n");
            else
                minutes.append("<option value=\"0" + i + "\">0" + i + "</option>\n");

        return true;
    }
    
    private static String GetExceptionFileName()
    {
        try
        {
            Date date = GetDate();
            DecimalFormat decimalformat = new DecimalFormat("#00");
            return decimalformat.format(date.getYear() + 1900) + "_" + decimalformat.format(date.getMonth() + 1) + "_" + decimalformat.format(date.getDate()) + ".log";
        }
        catch(Exception exception)
        {
            return "invalid filename " + exception.getMessage();
        }
    }

    private static Date GetDate()
    {
        try
        {
            Date date = new Date();
            return date;
        }
        catch(Exception _ex)
        {
            return null;
        }
    }

    public static void DumpException(String s, String s1, HttpServletRequest httpservletrequest, Exception exception, ServletContext servletContext)
    {
        String s2 = "";
        try
        {
            String s3 = GetExceptionFilePath(httpservletrequest, servletContext) + GetExceptionFileName();
        
            FileWriter filewriter = new FileWriter(s3, true);
            filewriter.write((new Date()).toString() + "^" + s + "^" + s1 + "^" + exception.getMessage() + "\r\n");
            PrintWriter printwriter = new PrintWriter(filewriter, true);
            exception.printStackTrace(printwriter);
            filewriter.write("\r\n");
            filewriter.flush();
            filewriter.close();
            printwriter.close();
        }
        catch(Exception _ex) { }
    }

    private static String GetExceptionFilePath(HttpServletRequest request)
    {
       // return "D:\\HTMLS\\SMSPortal\\logs\\";
        return "/opt/Htmls/logs/";
    }
    
    public static Connection getMysqlConn(ServletContext context)
    {
      try
      {
          String mysql_server = context.getInitParameter("mysql_server");
          String mysql_dbuser = "oe_2";
          String mysqlusr = context.getInitParameter("mysqlusr");
          String mysqlpwd = context.getInitParameter("mysqlpwd");
          Connection mysqlconn = null;
          Class.forName("com.mysql.jdbc.Driver").newInstance();
          //return DriverManager.getConnection("jdbc:mysql://127.0.0.1/oe?user=abc1234open&password=abc!@#$1234");
          return DriverManager.getConnection("jdbc:mysql://"+mysql_server+"/"+mysql_dbuser+"?user="+mysqlusr+"&password="+mysqlpwd+"");
//        Connection mysqlconn = null;
//        Class.forName("com.mysql.jdbc.Driver").newInstance();
//        return DriverManager.getConnection("jdbc:mysql://127.0.0.1/oe_2?user=oe&password=abc1234oe");
      }
      catch (Exception e) {}
      return null;
    }
    
    public static void DumException(String ClassName, String FuncName, HttpServletRequest request, Exception exp)
    {
        String FileName = "";
        String UserId = GetCookie("UserId", request);
        String UserIP = request.getRemoteAddr();
        try 
        {
            FileName = GetExceptionFilePath(request) + GetExceptionFileName();
            FileWriter fr = new FileWriter(FileName, true);
            fr.write("\r\n ===========================  EXCEPTION STARTS ========================  \r\n");
            fr.write((new Date()).toString() + "^" + UserId + "^" + UserIP + "^" + ClassName + "^" + FuncName + "^" + exp.getMessage() + "\r\n");
            PrintWriter pr = new PrintWriter(fr, true);
            exp.printStackTrace(pr);
            fr.write("\r\n ===========================  EXCEPTION ENDS ========================  \r\n");
            fr.flush();
            fr.close();
            pr.close();
        }
        catch(Exception exception) { }
    }
    
    public static String REC(String Str)
    {
        String rStr = "";
        rStr = Str.replace('\'', '`');
        return rStr;
    }

    
    public static void DumException(String ClassName, String FuncName, HttpServletRequest request, Exception exp, ServletContext servletContext)
    {
      String FileName = "";
      try
      {
        FileName = GetExceptionFilePath(request, servletContext) + GetExceptionFileName();
        FileWriter fr = new FileWriter(FileName, true);
        fr.write(new Date().toString() + "^" + ClassName + "^" + FuncName + "^" + exp.getMessage() + "\r\n");
        
        PrintWriter pr = new PrintWriter(fr, true);
        exp.printStackTrace(pr);
        fr.write("\r\n");
        fr.flush();
        fr.close();
        pr.close();
      }
      catch (Exception localException) {}
    }
    
    public static boolean checkSession(PrintWriter out, HttpServletRequest request)
    {
      if (request.getSession(false) == null)
      {
        out.println(" <center><table cellpadding=3 cellspacing=2><tr><td bgcolor=\"#FFFFFF\">  <font color=green face=arial><b>Your Session Has Been Expired</b></font>  </td></tr></table>  <p>  <font face=arial size=+1><b><a href=/oe/index.html target=_top> Return to OE Portal  </a></b></font> <br><font face=arial size=-2>(You will need to sign in again.)</font><br>  </center> ");
        
        return false;
      }
      return true;
    }
    
    
    public static int SendEmail11( String ToEmail, String ToEmail1, String ToEmail2, String EmailSubject, String HTMLText, PrintWriter out, ServletContext servletContext)
    {
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
      try
      {
    	 
    	  
    	  session.setDebug(true);
        MimeMessage message = new MimeMessage(session);
        

        message.setFrom(new InternetAddress(from));
        

        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        

        message.setSubject(EmailSubject);
        


        message.setContent(HTMLText, "text/html");
        

        Transport.send(message);
        
        return 1;
      }
      catch (Exception e)
      {
        out.println("Unable to send email :" + e.getMessage());
      }
      return -1;
    }

	public static String GetConnectString() {
		// TODO Auto-generated method stub
		 return "jdbc:mysql://127.0.0.1/asteriskcdrdb?user=abc1234open&password=abc!@#$1234";

	}
	
	
	public static int SendEmail( String ToEmail, String ToEmail1, String ToEmail2, String EmailSubject, String HTMLText, PrintWriter out, ServletContext servletContext) {
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

	
	public static Properties getEmailProperties() {
        final Properties config = new Properties();
        config.put("mail.smtp.auth", "true");
        config.put("mail.smtp.starttls.enable", "true");
        config.put("mail.smtp.host", "smtp.office365.com");
        config.put("mail.smtp.port", "587");
        return config;
    }
	
}
