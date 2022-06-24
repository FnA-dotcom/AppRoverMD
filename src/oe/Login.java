package oe;


import Parsehtm.Parsehtm;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;

@SuppressWarnings("Duplicates")
public class Login extends HttpServlet {

    private static final String CONTENT_TYPE = "text/html; charset=windows-1252";

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HandleRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HandleRequest(request, response);
    }

    public void HandleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Connection conn = null;
        String Action = null;
        PrintWriter out = new PrintWriter(response.getOutputStream());

        try {
            String constring = Services.ConnString(getServletContext(), 1);
            conn = Services.GetConnection(getServletContext(), 1);
            if (conn == null) {
                out.println("Unable to connect with Database...1");
                out.flush();
                out.close();
                return;
            }
        } catch (Exception excp) {
            conn = null;
            System.out.println("Exception excp conn: " + excp.getMessage());
            //out.println(excp.getMessage());
            //return;
        }

        ServletContext context = null;
        context = getServletContext();


        response.setContentType("text/html");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setHeader("Expires", "0"); // Proxies.
        try {
            if (request.getParameter("Action") == null) {
                boolean ValidSession = Services.checkSession(out, request);
                if (!ValidSession) {
                    out.flush();
                    out.close();
                    conn.close();
                    return;
                }
            }
            Action = request.getParameter("Action");

            if ((Action.compareTo("Login_old") != 0) && (Action.compareTo("Register") != 0) && (Action.compareTo("passwordreset") != 0) && (Action.compareTo("forgetpassword") != 0) && (Action.compareTo("passwordresetsave") != 0)) {
                boolean ValidSession = Services.checkSession(out, request);
                if (!ValidSession) {
                    out.flush();
                    out.close();
                    conn.close();
                    return;
                }
            }

            if (Action.compareTo("Login_old") == 0) {
                Login(request, response, out, conn, context);
            } else if (Action.compareTo("Logout") == 0) {
                Logout(request, response, out, conn, context);
            } else if (Action.compareTo("Register") == 0) {
                Register(request, response, out, conn, context);
            } else if (Action.compareTo("forgetpassword") == 0) {
                forgetpassword(request, response, out, conn, context);
            } else if (Action.compareTo("passwordreset") == 0) {
                passwordreset(request, response, out, conn, context);
            } else if (Action.compareTo("passwordresetsave") == 0) {
                passwordresetsave(request, response, out, conn, context);
            }

            //passwordresetsave
            else {
                out.println("<font size=\"3\" face=\"Calibri\">Under Development  " + Action + "</font>");
            }
            out.flush();
            out.close();

            conn.close();
        } catch (Exception e) {
            Services.DumException("Login_old", "HandleRequest", request, e, getServletContext());
            try {
                Cookie cookie = new Cookie("UserId", "");
                response.addCookie(cookie);

                String Message = "You have been logged out of the System.";

                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", Message);
                Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "index.html");
            } catch (Exception localException1) {
            }
        }
        out.flush();
        out.close();
    }

    public void Login(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext context1) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int i = 0;
        int Found = 0;
        String UNAME = "";
        int userindex = 0;
        String UserId = request.getParameter("UserId");
        String Password = request.getParameter("Passwd");

        try {

            if ((UserId == null) || (UserId.trim() == null) || (UserId.trim().length() < 1)) {
                throw new Exception("Enter Valid UserId.");
            }
            if ((Password == null) || (Password.trim() == null) || (Password.trim().length() == 0)) {
                throw new Exception("Enter Valid Password.");
            }

            String UID = "";
            String CompanyId = "";
            String UserType = "";
            String Enabled = "";
            Query = " select userid,username,enabled,usertype,indexptr from sysusers " +
                    "where upper(ltrim(rtrim(userid))) = '" + UserId.trim().toUpperCase() + "' and   " +
                    "password = '" + Password.trim() + "'";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            if (hrset.next()) {
                UID = hrset.getString(1).trim();
                UNAME = hrset.getString(2).trim();
                Enabled = hrset.getString(3).trim();
                UserType = hrset.getString(4).trim();
                userindex = hrset.getInt(5);

            } else {
                throw new Exception("Invalid UserId or Password!!!");
            }
            if (Enabled.compareTo("N") == 0) {
                throw new Exception("Your login has blocked , Contact OE Representative !!!");
            }
            if (Enabled.compareTo("W") == 0) {
                throw new Exception("Waiting for Approval , Contact OE Representative !!!");
            }
            hrset.close();
            hstmt.close();

            Cookie uid = new Cookie("UserId", UserId.trim());
            Cookie usertype = new Cookie("UserType", UserType.trim());
            Cookie username = new Cookie("username", UNAME.trim());
            Cookie uindex = new Cookie("userindex", Integer.toString(userindex));
            response.addCookie(uid);
            response.addCookie(usertype);
            response.addCookie(username);
            response.addCookie(uindex);

            ServletContext context = this.getServletContext();
            if (context.getAttribute("ActiveSessions") == null) {
                // create new Session
                HttpSession session = request.getSession(true);
                //out.print("ActiveSessions is NULL.<br>");
                Hashtable ht = new Hashtable();
                ht.put(UserId, session);
                context.setAttribute("ActiveSessions", ht);
            } else {
                //out.print("Modified ActiveSessions is NOT NULL.<br>" +
                //context.getAttribute("ActiveSessions").getClass().getName());
                Hashtable ht = (Hashtable) context.getAttribute("ActiveSessions");

                if (ht.get(UserId) != null) // single login allowed
                {
                    HttpSession session = (HttpSession) ht.get(UserId);
                    //session.invalidate();

                    ht.remove(UserId);
                    context.setAttribute("ActiveSessions", ht);
                }
                // create new Session
                HttpSession session = request.getSession(true);
                ht.put(UserId, session);
                context.setAttribute("ActiveSessions", ht);
            }
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserName", UNAME);
            if (UserType.equals("1")) { //reseller
                Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "WelcomeMenu.html");
            } else if (UserType.equals("9")) { //Admin
                Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "WelcomeMenuadmin.html");
            } else if (UserType.equals("3")) {
                Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "main_wallboard.html");
            } else if (UserType.equals("4")) {
                Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "ob_admin_main.html");
            } else if (UserType.equals("5")) {
                Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "ob_supervisor_main.html");
            } else if (UserType.equals("6")) { //Agent
                Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "WelcomeMenu_agent.html");
            }

        } catch (Exception e) {
            Services.DumException("Login_old", "Login_old", request, e, getServletContext());
            try {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", e.getMessage());
                Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "index.html");
            } catch (Exception localException1) {
            }
        }
    }

    public void Register(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext context1) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int i = 0;
        int Found = 0;
        String UNAME = "";
        String companyid = "";
        String UserId = request.getParameter("email");
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String email = UserId;
        String Password = request.getParameter("passwd1");
        String companyname = request.getParameter("companyname");
        String Password1 = request.getParameter("Passwd1");
        String contact = request.getParameter("contact");
        String AgentCode = request.getParameter("AgentCode");
        int EmailResult = 0;

        try {
	     /* if ((UserId == null) || (UserId.trim() == null) || (UserId.trim().length() < 1)) {
	        throw new Exception("Enter Email to proceed....");
	      }
	      if ((Password == null) || (Password.trim() == null) || (Password.trim().length() == 0)) {
	        throw new Exception("Enter  Password to proceed....");
	      }*/
            String UID = "";
            String CompanyId = "";
            String UserType = "";
            String Enabled = "";
            int found = 0;
            Query = " select count(*) from sysusers where upper(ltrim(rtrim(userid))) = '" + UserId.trim().toUpperCase() + "'" + " and   email = '" + UserId.trim() + "'";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            if (hrset.next()) {
                found = hrset.getInt(1);
                if (found == 1) {
                    throw new Exception("Email Address Already registered!!!");
                }
            }
            Query = " select max(companyid)+1 from sysusers";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            if (hrset.next()) {
                companyid = hrset.getString(1);
            }

            hrset.close();
            hstmt.close();
            int found2 = 0;
            Query = "select count(*) from oe.agentkey where agentcode='" + AgentCode + "' and agentid is null";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            if (hrset.next()) {
                found2 = hrset.getInt(1);
            }
            System.out.println(Query);
            System.out.println(found2);
            hrset.close();
            hstmt.close();
            if (found2 == 1) {
                Found = 0;
                UNAME = lastname;
                Query = " insert into oe.sysusers " +
                        " (userid, firstname, lastname,password,username,companyname, usertype, create_date, enabled, email,companyid) " +
                        " values " +
                        "('" + email + "','" + firstname + "','" + lastname + "','" + Password + "','" + lastname + " " + firstname + "','" + companyname + "',1, now(), 'W','" + email + "','" + companyid + "') ";
                hstmt = conn.createStatement();
                hstmt.executeUpdate(Query);
                hstmt.close();

                Query = " insert into company " +
                        " (companyname, companyid,createddate,createdby) " +
                        " values " +
                        "('" + companyname + "','" + companyid + "',now(),'" + email + "') ";
                hstmt = conn.createStatement();
                hstmt.executeUpdate(Query);
                hstmt.close();

                Query = " update agentkey set agentid='" + email + "',usedon=now() where agentcode='" + AgentCode + "'";
                hstmt = conn.createStatement();
                hstmt.executeUpdate(Query);
                hstmt.close();


                String EmailSubject = "OE Reseller Portal Welcome";
                String HTMLText = "<h1> Dear " + UNAME + "</h1>";
                HTMLText += "<h1> OUR ENERGY BEST TEXAS ENERGY AND ELECTRIC COMPANY SERVICES SINCE 2009</h1>";
                HTMLText += "<p>Your Request has been Submitted to OE Team, </p>";
                //  HTMLText += "<h2>Your Login_old ID is " + email + "<br>Your new password is <u>" + Password + "</u></h2>";
                HTMLText += "<p>You will received Approval Email at " + email + ". </p>";
                HTMLText += "<p></p>";
                HTMLText += "<p>Thank you<br>Regards,<br> Our Energy LLC</p>";


                EmailResult = Services.SendEmail("alisaadbaig@gmail.com", email, email, EmailSubject, HTMLText, out, getServletContext());

                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserName", UNAME);
                Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "successregister.html");

            } else {
                // EmailResult = Services.SendEmail("alisaadbaig@gmail.com", email,email, "test email", "hello", out, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserName", UNAME);
                Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "Unsuccessregister.html");


            }

        } catch (Exception e) {
            //out.println(e.getMessage());
            try {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", e.getMessage());
                Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "register.html");
            } catch (Exception localException1) {
            }
        }
    }

    public void forgetpassword(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext context1) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int i = 0;
        int Found = 0;
        String UNAME = "";
        String UserId = request.getParameter("UserId");

        try {
            Parsehtm Parser = new Parsehtm(request);
            if ((UserId == null) || (UserId.trim() == null) || (UserId.trim().length() < 1)) {
                throw new Exception("Enter UserId and  to proceed....");
            }

            String UID = "";
            String CompanyId = "";
            String UserType = "";
            String Enabled = "";
            Query = " select userid,username,enabled,usertype from sysusers where upper(ltrim(rtrim(email))) = '" + UserId.trim().toUpperCase() + "'";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            if (hrset.next()) {
                UID = hrset.getString(1).trim();
                UNAME = hrset.getString(2).trim();
                Enabled = hrset.getString(3).trim();
                UserType = hrset.getString(4).trim();

            } else {
                //Parser.SetField("UserName", UNAME);
                // Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "forgetpasswordlink.html");

                throw new Exception("Invalid Email Address!!!");
            }

            KeyGenerator gen = KeyGenerator.getInstance("AES");
            gen.init(128); /* 128-bit AES */
            SecretKey secret = gen.generateKey();
            byte[] binary = secret.getEncoded();
            String token = String.format("%032X", new BigInteger(+1, binary));


            Query = " insert into passwordtoken " +
                    " (token, Createdon,email) " +
                    " values " +
                    "('" + token + "',now(),'" + UserId + "') ";
            hstmt = conn.createStatement();
            hstmt.executeUpdate(Query);
            hstmt.close();


            int EmailResult = 0;
            String EmailSubject = "OE Reseller Portal Password Reset ";
            String HTMLText = "<h1> Dear " + UNAME + "</h1>";
            HTMLText += "<h1> OUR ENERGY BEST TEXAS ENERGY AND ELECTRIC COMPANY SERVICES SINCE 2009</h1>";
            HTMLText += "<p>Kindly Click on below link ,</p>";
            HTMLText += "<h2><a href=http://203.130.0.235:84/oe/oe.Login_old?Action=passwordreset&token=" + token + ">Reset Password</a></u></h2>";
            HTMLText += "<p>Above Link is valid for 24 hours only. </p>";
            HTMLText += "<p></p>";
            HTMLText += "<p>Thank you<br>Regards,<br> Our Energy LLC</p>";

            EmailResult = Services.SendEmail(UserId, UserId, UserId, EmailSubject, HTMLText, out, getServletContext());


            Parser.SetField("UserName", UNAME);
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "forgetpasswordlink.html");

        } catch (Exception e) {
            try {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", e.getMessage());
                Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "index.html");
            } catch (Exception localException1) {
            }
        }
    }


    public void passwordreset(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext context1) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int i = 0;
        int Found = 0;
        String UNAME = "";
        String token = request.getParameter("token");
        String email = "";

        try {
            if ((token == null) || (token.trim() == null) || (token.trim().length() < 1)) {
                throw new Exception("token Invalid Token ....");
            }

            String UID = "";
            String CompanyId = "";
            String UserType = "";
            String Enabled = "";
            Query = " select email from passwordtoken where valid=0 and token= '" + token + "'";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            if (hrset.next()) {
                email = hrset.getString(1).trim();


            } else {
                throw new Exception("Invalid Token , please go to forget password link and reset your password!!!");
            }

            Query = " select userid,username,enabled,usertype from sysusers where upper(ltrim(rtrim(userid))) = '" + email.trim().toUpperCase() + "'";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            if (hrset.next()) {
                UID = hrset.getString(1).trim();
                UNAME = hrset.getString(2).trim();
                Enabled = hrset.getString(3).trim();
                UserType = hrset.getString(4).trim();

            } else {
                throw new Exception("Invalid Email Address!!!");
            }


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserName", UNAME);
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "resetpassword.html");

        } catch (Exception e) {
            try {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", e.getMessage());
                Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "index.html");
            } catch (Exception localException1) {
            }
        }
    }

    public void passwordresetsave(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext context1) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int i = 0;
        int Found = 0;
        String UNAME = "";
        String password = request.getParameter("password");
        String password1 = request.getParameter("password1");
        String token = request.getParameter("token");
        String email = "";

        try {
            if ((password == null) || (password.trim() == null) || (password.trim().length() < 1)) {
                throw new Exception("Enter UserId and  to proceed....");
            }
            if ((password1 == null) || (password1.trim() == null) || (password1.trim().length() < 1)) {
                throw new Exception("Enter UserId and  to proceed....");
            }
            if (password.compareTo(password1) != 0) {
                throw new Exception("Please Re-Enter Password Mismatch ....");
            }

            Query = " select email from passwordtoken where token= '" + token + "'";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            if (hrset.next()) {
                email = hrset.getString(1).trim();


            }


            Query = " update sysusers set password='" + password + "' where email='" + email + "'";
            hstmt = conn.createStatement();
            hstmt.executeUpdate(Query);
            hstmt.close();

            Query = " update passwordtoken set valid='1',usedon=now() where email='" + email + "'";
            hstmt = conn.createStatement();
            hstmt.executeUpdate(Query);
            hstmt.close();


            int EmailResult = 0;
            String EmailSubject = "OE Reseller Portal Password Reset done ";
            String HTMLText = "<h1> Dear " + UNAME + "</h1>";
            HTMLText += "<h1> OUR ENERGY BEST TEXAS ENERGY AND ELECTRIC COMPANY SERVICES SINCE 2009</h1>";
            HTMLText += "<p>Password has been Reset</p>";

            HTMLText += "<p></p>";
            HTMLText += "<p>Thank you<br>Regards,<br> Our Energy LLC</p>";

            EmailResult = Services.SendEmail(email, email, email, EmailSubject, HTMLText, out, getServletContext());


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserName", UNAME);
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "resetpassworddone.html");

        } catch (Exception e) {
            try {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", e.getMessage());
                Parser.SetField("token", token);
                Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "resetpassword.html");
            } catch (Exception localException1) {
            }
        }
    }


    public void Logout(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Connection conn, ServletContext context) {

        String UserId = Services.GetCookie("UserId", request);
        try {

            HttpSession session = request.getSession(false);

            session.invalidate();

            ServletContext servletcontext = this.getServletContext();

            if (context.getAttribute("ActiveSessions") != null) {
                Hashtable ht = (Hashtable) servletcontext.getAttribute("ActiveSessions");

                ht.remove(UserId);
                context.setAttribute("ActiveSessions", ht);
            }

            String Message = "You have been successfully logged out from the System.";

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Error", Message);
            Parser.GenerateHtml(out, Services.GetHtmlPath(getServletContext()) + "index.html");
        } catch (Exception e) {
            try {
                Services.DumException("Login_old", "Logout", request, e, this.getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", e.getMessage());
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "index.html");
            } catch (Exception exception) {
            }
        }
    }
}