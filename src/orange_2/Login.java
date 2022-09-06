// 
// Decompiled by Procyon v0.5.36
// 

package orange_2;

import Parsehtm.Parsehtm;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;

public class Login extends HttpServlet
{
    private static final String CONTENT_TYPE = "text/html; charset=windows-1252";

    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.HandleRequest(request, response);
    }

    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.HandleRequest(request, response);
    }

    public void HandleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        String Action = null;
        final PrintWriter out = new PrintWriter((OutputStream)response.getOutputStream());
        try {
            final String constring = Services.ConnString(this.getServletContext(), 1);
            conn = Services.GetConnection(this.getServletContext(), 1);
            if (conn == null) {
                out.println("Unable to connect with Database...1");
                //out.println(constring);
                out.flush();
                out.close();
                return;
            }
        }
        catch (Exception excp) {
            conn = null;
            System.out.println("Exception excp conn: " + excp.getMessage());
        }
        ServletContext context = null;
        context = this.getServletContext();
        response.setContentType("text/html");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        try {
            if (request.getParameter("Action") == null) {
                final boolean ValidSession = Services.checkSession(out, request);
                if (!ValidSession) {
                    out.flush();
                    out.close();
                    conn.close();
                    return;
                }
            }
            Action = request.getParameter("Action");
            if (Action.compareTo("Login") != 0 && Action.compareTo("Register") != 0 && Action.compareTo("passwordreset") != 0 && Action.compareTo("forgetpassword") != 0 && Action.compareTo("passwordresetsave") != 0 && Action.compareTo("LoginAdmin") != 0) {
                final boolean ValidSession = Services.checkSession(out, request);
                if (!ValidSession) {
                    out.flush();
                    out.close();
                    conn.close();
                    return;
                }
            }
            if (Action.compareTo("Login") == 0) {
                this.Login(request, response, out, conn, context);
            }
            else if (Action.compareTo("LoginAdmin") == 0) {
                this.LoginAdmin(request, response, out, conn, context);
            }
            else if (Action.compareTo("Logout") == 0) {
                this.Logout(request, response, out, conn, context);
            }
            else if (Action.compareTo("Register") == 0) {
                this.Register(request, response, out, conn, context);
            }
            else if (Action.compareTo("forgetpassword") == 0) {
                this.forgetpassword(request, response, out, conn, context);
            }
            else if (Action.compareTo("passwordreset") == 0) {
                this.passwordreset(request, response, out, conn, context);
            }
            else if (Action.compareTo("passwordresetsave") == 0) {
                this.passwordresetsave(request, response, out, conn, context);
            }
            out.flush();
            out.close();
            conn.close();
        }
        catch (Exception e) {
            Services.DumException("Login", "HandleRequest", request, e, this.getServletContext());
            try {
                final Cookie cookie = new Cookie("UserId", "");
                response.addCookie(cookie);
                final String Message = "You have been logged out of the System.";
                final Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "You have been logged out of the System.");
                Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "index.html");
            }
            catch (Exception ex) {}
        }
        out.flush();
        out.close();
    }

    public void Login(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final ServletContext context1) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        final int i = 0;
        final int Found = 0;
        int ClientIndex = 0;
        String ClientId = "";
        String UNAME = "";
        int userindex = 0;
        final String UserId = request.getParameter("UserId");
        final String Password = request.getParameter("Passwd");
        try {
            if (UserId == null || UserId.trim() == null || UserId.trim().length() < 1) {
                throw new Exception("Enter Valid UserId.");
            }
            if (Password == null || Password.trim() == null || Password.trim().length() == 0) {
                throw new Exception("Enter Valid Password.");
            }
            String UID = "";
            final String CompanyId = "";
            String UserType = "";
            String Enabled = "";
            Query = " select userid,username,enabled,usertype,indexptr,IFNULL(clientid,0) from sysusers where upper(ltrim(rtrim(userid))) = '" + UserId.trim().toUpperCase() + "'" + " and   password = '" + Password.trim() + "'";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            if (!hrset.next()) {
                throw new Exception("Invalid UserId or Password!!!");
            }
            UID = hrset.getString(1).trim();
            UNAME = hrset.getString(2).trim();
            Enabled = hrset.getString(3).trim();
            UserType = hrset.getString(4).trim();
            userindex = hrset.getInt(5);
            ClientIndex = hrset.getInt(6);
            if (Enabled.compareTo("N") == 0) {
                throw new Exception("Your login has blocked , Contact OE Representative !!!");
            }
            if (Enabled.compareTo("W") == 0) {
                throw new Exception("Waiting for Approvel , Contact OE Representative !!!");
            }
            hrset.close();
            hstmt.close();
            final Cookie uid = new Cookie("UserId", UserId.trim());
            final Cookie usertype = new Cookie("UserType", UserType.trim());
            final Cookie username = new Cookie("username", UNAME.trim());
            final Cookie uindex = new Cookie("userindex", Integer.toString(userindex));
            response.addCookie(uid);
            response.addCookie(usertype);
            response.addCookie(username);
            response.addCookie(uindex);
            final ServletContext context2 = this.getServletContext();
            if (context2.getAttribute("ActiveSessions") == null) {
                final HttpSession session = request.getSession(true);
                final Hashtable ht = new Hashtable();
                ht.put(UserId, session);
                context2.setAttribute("ActiveSessions", (Object)ht);
            }
            else {
                final Hashtable ht2 = (Hashtable)context2.getAttribute("ActiveSessions");
                if (ht2.get(UserId) != null) {
                    final HttpSession session2 = (HttpSession) ht2.get(UserId);
                    ht2.remove(UserId);
                    context2.setAttribute("ActiveSessions", (Object)ht2);
                }
                final HttpSession session2 = request.getSession(true);
                ht2.put(UserId, session2);
                context2.setAttribute("ActiveSessions", (Object)ht2);
            }
            Query = "SELECT name FROM oe.clients WHERE Id = " + ClientIndex;
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            if (hrset.next()) {
                ClientId = hrset.getString(1).trim();
            }
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserName", UNAME);
            Parser.SetField("ClientId", String.valueOf(ClientId));
            Parser.SetField("UserType", String.valueOf(UserType));
            if (UserType.equals("1")) {
                Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "WelcomeMenu.html");
            }
            else if (UserType.equals("9")) {
                Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "WelcomeMenuadmin.html");
            }
            else if (UserType.equals("3")) {
                Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "main_wallboard.html");
            }
            else if (UserType.equals("4")) {
                if (ClientIndex == 8) {
                    Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "ob_admin_main.html");
                }
                else if (ClientIndex == 9) {
                    Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "ob_admin_main_Victoria.html");
                }
                else if (ClientIndex == 10) {
                    Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "ob_admin_main_Oddasa.html");
                }
                else if (ClientIndex == 12) {
                    Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "ob_admin_main_saustin.html");
                }
            }
            else if (UserType.equals("5")) {
                Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "ob_supervisor_main.html");
            }
            else if (UserType.equals("6")) {
                Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "WelcomeMenu_agent.html");
            }
        }
        catch (Exception e) {
            Services.DumException("Login", "Login", request, e, this.getServletContext());
            try {
                final Parsehtm Parser2 = new Parsehtm(request);
                Parser2.SetField("Error", e.getMessage());
                Parser2.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "index.html");
            }
            catch (Exception ex) {}
        }
    }

    public void LoginAdmin(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final ServletContext context1) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        final int i = 0;
        final int Found = 0;
        String ClientId = "";
        String UNAME = "";
        int userindex = 0;
        String UserId = request.getParameter("UserId");
        final String Password = request.getParameter("Passwd");
        final int ClientIndex = Integer.parseInt(request.getParameter("ClientIndex"));
        System.out.println("Stage 1 Here");
        try {
            if (UserId == null || UserId.trim() == null || UserId.trim().length() < 1) {
                throw new Exception("Enter Valid UserId.");
            }
            if (Password == null || Password.trim() == null || Password.trim().length() == 0) {
                throw new Exception("Enter Valid Password.");
            }
            String UID = "";
            final String CompanyId = "";
            String UserType = "";
            String Enabled = "";
            Query = " select userid,username,enabled,usertype,indexptr from sysusers where upper(ltrim(rtrim(userid))) = '" + UserId.trim().toUpperCase() + "'" + " and   password = '" + Password.trim() + "'";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            if (!hrset.next()) {
                throw new Exception("Invalid UserId or Password!!!");
            }
            UID = hrset.getString(1).trim();
            UNAME = hrset.getString(2).trim();
            Enabled = hrset.getString(3).trim();
            UserType = hrset.getString(4).trim();
            userindex = hrset.getInt(5);
            if (Enabled.compareTo("N") == 0) {
                throw new Exception("Your login has blocked , Contact OE Representative !!!");
            }
            if (Enabled.compareTo("W") == 0) {
                throw new Exception("Waiting for Approvel , Contact OE Representative !!!");
            }
            if (UserType.equals("7")) {
                Query = "Select userid from oe.sysusers where clientid = " + ClientIndex;
                hstmt = conn.createStatement();
                hrset = hstmt.executeQuery(Query);
                if (hrset.next()) {
                    UserId = hrset.getString(1);
                }
            }
            hrset.close();
            hstmt.close();
            final Cookie uid = new Cookie("UserId", UserId.trim());
            final Cookie usertype = new Cookie("UserType", UserType.trim());
            final Cookie username = new Cookie("username", UNAME.trim());
            final Cookie uindex = new Cookie("userindex", Integer.toString(userindex));
            response.addCookie(uid);
            response.addCookie(usertype);
            response.addCookie(username);
            response.addCookie(uindex);
            final ServletContext context2 = this.getServletContext();
            if (context2.getAttribute("ActiveSessions") == null) {
                final HttpSession session = request.getSession(true);
                final Hashtable ht = new Hashtable();
                ht.put(UserId, session);
                context2.setAttribute("ActiveSessions", (Object)ht);
            }
            else {
                final Hashtable ht2 = (Hashtable)context2.getAttribute("ActiveSessions");
                if (ht2.get(UserId) != null) {
                    final HttpSession session2 = (HttpSession) ht2.get(UserId);
                    ht2.remove(UserId);
                    context2.setAttribute("ActiveSessions", (Object)ht2);
                }
                final HttpSession session2 = request.getSession(true);
                ht2.put(UserId, session2);
                context2.setAttribute("ActiveSessions", (Object)ht2);
            }
            Query = "SELECT name FROM oe.clients WHERE Id = " + ClientIndex;
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            if (hrset.next()) {
                ClientId = hrset.getString(1).trim();
            }
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserName", UNAME);
            Parser.SetField("ClientId", String.valueOf(ClientId));
            Parser.SetField("UserType", String.valueOf(UserType));
            if (UserType.equals("7")) {
                if (ClientIndex == 8) {
                    Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "ob_admin_main.html");
                }
                else if (ClientIndex == 9) {
                    Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "ob_admin_main_Victoria.html");
                }
                else if (ClientIndex == 10) {
                    Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "ob_admin_main_Oddasa.html");
                }
                else if (ClientIndex == 12) {
                    Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "ob_admin_main_saustin.html");
                }
            }
        }
        catch (Exception e) {
            Services.DumException("Login", "Login", request, e, this.getServletContext());
            try {
                final Parsehtm Parser2 = new Parsehtm(request);
                Parser2.SetField("Error", e.getMessage());
                Parser2.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "index.html");
            }
            catch (Exception ex) {}
        }
    }

    public void Register(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final ServletContext context1) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        final int i = 0;
        int Found = 0;
        String UNAME = "";
        String companyid = "";
        final String UserId = request.getParameter("email");
        final String firstname = request.getParameter("firstname");
        final String lastname = request.getParameter("lastname");
        final String email = UserId;
        final String Password = request.getParameter("passwd1");
        final String companyname = request.getParameter("companyname");
        final String Password2 = request.getParameter("Passwd1");
        final String contact = request.getParameter("contact");
        final String AgentCode = request.getParameter("AgentCode");
        int EmailResult = 0;
        try {
            final String UID = "";
            final String CompanyId = "";
            final String UserType = "";
            final String Enabled = "";
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
                Query = " insert into oe.sysusers  (userid, firstname, lastname,password,username,companyname, usertype, create_date, enabled, email,companyid)  values ('" + email + "','" + firstname + "','" + lastname + "','" + Password + "','" + lastname + " " + firstname + "','" + companyname + "',1, now(), 'W','" + email + "','" + companyid + "') ";
                hstmt = conn.createStatement();
                hstmt.executeUpdate(Query);
                hstmt.close();
                Query = " insert into company  (companyname, companyid,createddate,createdby)  values ('" + companyname + "','" + companyid + "',now(),'" + email + "') ";
                hstmt = conn.createStatement();
                hstmt.executeUpdate(Query);
                hstmt.close();
                Query = " update agentkey set agentid='" + email + "',usedon=now() where agentcode='" + AgentCode + "'";
                hstmt = conn.createStatement();
                hstmt.executeUpdate(Query);
                hstmt.close();
                final String EmailSubject = "OE Reseller Portal Welcome";
                String HTMLText = "<h1> Dear " + UNAME + "</h1>";
                HTMLText += "<h1> OUR ENERGY BEST TEXAS ENERGY AND ELECTRIC COMPANY SERVICES SINCE 2009</h1>";
                HTMLText += "<p>Your Request has been Submitted to OE Team, </p>";
                HTMLText = HTMLText + "<p>You will received Approval Email at " + email + ". </p>";
                HTMLText += "<p></p>";
                HTMLText += "<p>Thank you<br>Regards,<br> Our Energy LLC</p>";
                EmailResult = Services.SendEmail("alisaadbaig@gmail.com", email, email, "OE Reseller Portal Welcome", HTMLText, out, this.getServletContext());
                final Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserName", UNAME);
                Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "successregister.html");
            }
            else {
                final Parsehtm Parser2 = new Parsehtm(request);
                Parser2.SetField("UserName", UNAME);
                Parser2.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "Unsuccessregister.html");
            }
        }
        catch (Exception e) {
            try {
                final Parsehtm Parser3 = new Parsehtm(request);
                Parser3.SetField("Error", e.getMessage());
                Parser3.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "register.html");
            }
            catch (Exception ex) {}
        }
    }

    public void forgetpassword(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final ServletContext context1) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        final int i = 0;
        final int Found = 0;
        String UNAME = "";
        final String UserId = request.getParameter("UserId");
        try {
            final Parsehtm Parser = new Parsehtm(request);
            if (UserId == null || UserId.trim() == null || UserId.trim().length() < 1) {
                throw new Exception("Enter UserId and  to proceed....");
            }
            String UID = "";
            final String CompanyId = "";
            String UserType = "";
            String Enabled = "";
            Query = " select userid,username,enabled,usertype from sysusers where upper(ltrim(rtrim(email))) = '" + UserId.trim().toUpperCase() + "'";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            if (!hrset.next()) {
                throw new Exception("Invalid Email Address!!!");
            }
            UID = hrset.getString(1).trim();
            UNAME = hrset.getString(2).trim();
            Enabled = hrset.getString(3).trim();
            UserType = hrset.getString(4).trim();
            final KeyGenerator gen = KeyGenerator.getInstance("AES");
            gen.init(128);
            final SecretKey secret = gen.generateKey();
            final byte[] binary = secret.getEncoded();
            final String token = String.format("%032X", new BigInteger(1, binary));
            Query = " insert into passwordtoken  (token, Createdon,email)  values ('" + token + "',now(),'" + UserId + "') ";
            hstmt = conn.createStatement();
            hstmt.executeUpdate(Query);
            hstmt.close();
            int EmailResult = 0;
            final String EmailSubject = "OE Reseller Portal Password Reset ";
            String HTMLText = "<h1> Dear " + UNAME + "</h1>";
            HTMLText += "<h1> OUR ENERGY BEST TEXAS ENERGY AND ELECTRIC COMPANY SERVICES SINCE 2009</h1>";
            HTMLText += "<p>Kindly Click on below link ,</p>";
            HTMLText = HTMLText + "<h2><a href=http://203.130.0.235:84/oe/oe.Login?Action=passwordreset&token=" + token + ">Reset Password</a></u></h2>";
            HTMLText += "<p>Above Link is valid for 24 hours only. </p>";
            HTMLText += "<p></p>";
            HTMLText += "<p>Thank you<br>Regards,<br> Our Energy LLC</p>";
            EmailResult = Services.SendEmail(UserId, UserId, UserId, "OE Reseller Portal Password Reset ", HTMLText, out, this.getServletContext());
            Parser.SetField("UserName", UNAME);
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "forgetpasswordlink.html");
        }
        catch (Exception e) {
            try {
                final Parsehtm Parser2 = new Parsehtm(request);
                Parser2.SetField("Error", e.getMessage());
                Parser2.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "index.html");
            }
            catch (Exception ex) {}
        }
    }

    public void passwordreset(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final ServletContext context1) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        final int i = 0;
        final int Found = 0;
        String UNAME = "";
        final String token = request.getParameter("token");
        String email = "";
        try {
            if (token == null || token.trim() == null || token.trim().length() < 1) {
                throw new Exception("token Invalid Token ....");
            }
            String UID = "";
            final String CompanyId = "";
            String UserType = "";
            String Enabled = "";
            Query = " select email from passwordtoken where valid=0 and token= '" + token + "'";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            if (!hrset.next()) {
                throw new Exception("Invalid Token , please go to forget password link and reset your password!!!");
            }
            email = hrset.getString(1).trim();
            Query = " select userid,username,enabled,usertype from sysusers where upper(ltrim(rtrim(userid))) = '" + email.trim().toUpperCase() + "'";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            if (!hrset.next()) {
                throw new Exception("Invalid Email Address!!!");
            }
            UID = hrset.getString(1).trim();
            UNAME = hrset.getString(2).trim();
            Enabled = hrset.getString(3).trim();
            UserType = hrset.getString(4).trim();
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserName", UNAME);
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "resetpassword.html");
        }
        catch (Exception e) {
            try {
                final Parsehtm Parser2 = new Parsehtm(request);
                Parser2.SetField("Error", e.getMessage());
                Parser2.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "index.html");
            }
            catch (Exception ex) {}
        }
    }

    public void passwordresetsave(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final ServletContext context1) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        final int i = 0;
        final int Found = 0;
        final String UNAME = "";
        final String password = request.getParameter("password");
        final String password2 = request.getParameter("password1");
        final String token = request.getParameter("token");
        String email = "";
        try {
            if (password == null || password.trim() == null || password.trim().length() < 1) {
                throw new Exception("Enter UserId and  to proceed....");
            }
            if (password2 == null || password2.trim() == null || password2.trim().length() < 1) {
                throw new Exception("Enter UserId and  to proceed....");
            }
            if (password.compareTo(password2) != 0) {
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
            final String EmailSubject = "OE Reseller Portal Password Reset done ";
            String HTMLText = "<h1> Dear </h1>";
            HTMLText += "<h1> OUR ENERGY BEST TEXAS ENERGY AND ELECTRIC COMPANY SERVICES SINCE 2009</h1>";
            HTMLText += "<p>Password has been Reset</p>";
            HTMLText += "<p></p>";
            HTMLText += "<p>Thank you<br>Regards,<br> Our Energy LLC</p>";
            EmailResult = Services.SendEmail(email, email, email, "OE Reseller Portal Password Reset done ", HTMLText, out, this.getServletContext());
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserName", "");
            Parser.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "resetpassworddone.html");
        }
        catch (Exception e) {
            try {
                final Parsehtm Parser2 = new Parsehtm(request);
                Parser2.SetField("Error", e.getMessage());
                Parser2.SetField("token", token);
                Parser2.GenerateHtml(out, Services.GetHtmlPath(this.getServletContext()) + "resetpassword.html");
            }
            catch (Exception ex) {}
        }
    }

    public void Logout(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter out, final Connection conn, final ServletContext context) {
        Statement hstmt = null;
        ResultSet hrset = null;
        String Query = "";
        int clientid = 0;
        final String UserId = Services.GetCookie("UserId", request);
        final String usertype = Services.GetCookie("UserType", request);
        try {
            Query = "Select clientid from oe.sysusers where ltrim(rtrim(UPPER(userid))) = ltrim(rtrim(UPPER('" + UserId + "')))";
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            if (hrset.next()) {
                clientid = hrset.getInt(1);
            }
            final HttpSession session = request.getSession(false);
            session.invalidate();
            final ServletContext servletcontext = this.getServletContext();
            if (context.getAttribute("ActiveSessions") != null) {
                final Hashtable ht = (Hashtable)servletcontext.getAttribute("ActiveSessions");
                ht.remove(UserId);
                context.setAttribute("ActiveSessions", (Object)ht);
            }
            final String Message = "You have been successfully logged out from the System.";
            System.out.println(clientid);
            System.out.println("usertype:-" + usertype);
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Error", "You have been successfully logged out from the System.");
            if (usertype.equals("7")) {
                System.out.println("Inside");
                Parser.GenerateHtml(out, "/opt/apache-tomcat-7.0.65/webapps/orange_2/index.html");
            }
            else if (clientid == 8) {
                System.out.println(this.getServletContext() + "loginVictoria.html");
                Parser.GenerateHtml(out, "/opt/apache-tomcat-7.0.65/webapps/orange_2/loginOrange.html");
            }
            else if (clientid == 9) {
                System.out.println(this.getServletContext() + "loginVictoria.html");
                Parser.GenerateHtml(out, "/opt/apache-tomcat-7.0.65/webapps/orange_2/loginVictoria.html");
            }
            else if (clientid == 10) {
                System.out.println(this.getServletContext() + "loginVictoria.html");
                Parser.GenerateHtml(out, "/opt/apache-tomcat-7.0.65/webapps/orange_2/loginOddasa.html");
            }
            else if (clientid == 12) {
                System.out.println(this.getServletContext() + "loginSouthAustin.html");
                Parser.GenerateHtml(out, "/opt/apache-tomcat-7.0.65/webapps/orange_2/loginSAustin.html");
            }
        }
        catch (Exception e) {
            out.println(e.getMessage());
            try {
                Services.DumException("Login", "Logout", request, e, this.getServletContext());
                final Parsehtm Parser2 = new Parsehtm(request);
                Parser2.SetField("Error", e.getMessage());
                Parser2.GenerateHtml(out, Services.GetHtmlPath(context) + "index.html");
            }
            catch (Exception exception) {
                out.println(exception.getMessage());
            }
        }
    }
}
