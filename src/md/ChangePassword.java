package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("Duplicates")
public class ChangePassword extends HttpServlet {
    Integer ScreenIndex = 30;
    private Connection conn = null;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        this.handleRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        this.handleRequest(request, response);
    }


    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String Action;

        ServletContext context;
        context = getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        String UserId;
        int FacilityIndex;
        int UserIndex;
        String DatabaseName;
        try {
            HttpSession session = request.getSession(false);
            UtilityHelper helper = new UtilityHelper();
            boolean validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }

            UserId = session.getAttribute("UserId").toString();
            DatabaseName = session.getAttribute("DatabaseName").toString();
            FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
            UserIndex = Integer.parseInt(session.getAttribute("UserIndex").toString());

            if (UserId.equals("")) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }
            Action = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);

/*            if(!helper.AuthorizeScreen(request,out,conn,context,UserId,this.ScreenIndex)){
//                out.println("You are not Authorized to access this page");
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "You are not Authorized to access this page");
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/Message.html");
                return;
            }*/

            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            switch (Action) {
                case "GetInput":
                    GetInput(request, out, conn, context, UserId, FacilityIndex, helper);
                    break;
                case "saveChangePassword":
                    saveChangePassword(request, out, conn, context, UserId, helper, FacilityIndex, UserIndex);
                    break;
                default:
                    helper.deleteUserSession(request, conn, session.getId());
                    //Invalidating Session.
                    session.invalidate();
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
                    break;
            }
        } catch (Exception e) {
            Parsehtm Parser = new Parsehtm(request);
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
            Services.DumException("ChangePasswordMain", "handleRequest", request, e, context);
            out.flush();
            out.close();
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

    private void GetInput(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, int facilityIndex, UtilityHelper helper) {
        try {
            String[] UserDetails = helper.loginUserDetails(request, conn, UserId, servletContext);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("UserName", UserDetails[0]);
            Parser.SetField("ClientName", UserDetails[3]);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ChangePasswordInput.html");
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }

    private void saveChangePassword(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, UtilityHelper helper, int facilityIndex, int userIndex) {
        String CurrPassword = request.getParameter("CurrPassword").trim();
        String NewPassword = request.getParameter("NewPassword").trim();
        String NewRPassword = request.getParameter("NewRPassword").trim();
        String passwordEnc = "";
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        boolean LowerFound = false;
        boolean NumericFound = false;
        boolean UpperFound = false;
        int CategoriesFound = 0;
        boolean AlreadyUsed = false;
        try {
            UserId = helper.getAdvocateLoginId(request, conn, servletContext, userIndex);
            passwordEnc = FacilityLogin.encrypt(CurrPassword);

            String previousPassword = "";
            String username = "";
            Query = "SELECT password,username FROM oe.sysusers WHERE userid = '" + UserId + "' ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                previousPassword = rset.getString(1);
                username = rset.getString(2);
            }
            rset.close();
            stmt.close();

            String ClientName = "";
            Query = "SELECT name FROM oe.clients WHERE Id = " + facilityIndex;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next())
                ClientName = rset.getString(1).trim();
            rset.close();
            stmt.close();

            //String[] UserDetails = helper.loginUserDetails(request, conn, UserId, servletContext);
            //out.println("passwordEnc " + UserDetails[4] + " <br>");
            if (!passwordEnc.equals(previousPassword)) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserName", username);
                Parser.SetField("ClientName", ClientName);
                Parser.SetField("Error", "Current Password Does not Match!!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ChangePasswordInput.html");
                return;
            }

            if (!NewPassword.equals(NewRPassword)) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserName", username);
                Parser.SetField("ClientName", ClientName);
                Parser.SetField("Error", "Password Mismatch, Please Enter Again ...!!!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ChangePasswordInput.html");
                return;
            }

            String ar[] = helper.MasterConfig(conn, request, servletContext);
            String exp_period = "";
            String wrong_pass = "";
            String pass_history = "";
            String enforced_pass = "";
            String app_policy = "";

            exp_period = ar[0];
            wrong_pass = ar[1];
            pass_history = ar[2];
            enforced_pass = ar[3];
            app_policy = ar[4];

            if (NewPassword == null || NewPassword.trim() == null || NewPassword.trim().length() < 8) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "Password cannot be left empty or it should be minimum of 8 characters.");
                Parser.SetField("FormName", "ChangePassword");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");
                return;
            }


            // Numeric check
            int Numeric = 0;
            for (int i = 0; i < NewPassword.length(); i++) {
                Numeric = (int) NewPassword.charAt(i);
                if ((Numeric >= 48 && Numeric <= 57)) {
                    NumericFound = true;
                    CategoriesFound++;
//                    System.out.println("NUMERIC FOUND ++ CAT --> 1");
                    break;
                }
            }

            // UpperCase check
            int UpperCase = 0;

            for (int i = 0; i < NewPassword.length(); i++) {
                UpperCase = (int) NewPassword.charAt(i);

                if ((UpperCase >= 65 && UpperCase <= 90)) {
                    UpperFound = true;
                    CategoriesFound++;
//                    System.out.println("UPPER CASE FOUND ++ CAT --> 2");
                    break;
                }
            }

            // Lowercase check
            int LowerCase = 0;

            for (int i = 0; i < NewPassword.length(); i++) {
                LowerCase = (int) NewPassword.charAt(i);

                if ((LowerCase >= 97 && LowerCase <= 122)) {
                    LowerFound = true;
                    CategoriesFound++;
//                    System.out.println("LOWER CASE FOUND ++ CAT --> 3");
                    break;
                }
            }

//            System.out.println("NewPassword " + NewPassword);
            String pattern1 = "(?=.*[@#$%^_+=!*&]).{8,}";
//            System.out.println("Pattern is --> " + pattern1);
            if (NewPassword.matches(pattern1)) {
//                System.out.println("PATTERN MATCHES++ CAT --> 4");
                CategoriesFound++;
            }


            int Useridfound = 0;
            Pattern pattern = Pattern.compile(UserId, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(NewPassword);
            while (matcher.find()) {
//                System.out.println("TABISH "+matcher.group());
                Useridfound = 1;
            }
//            System.out.println("Useridfound " + Useridfound);
//            System.out.println("CategoriesFound " + CategoriesFound);
            if (Useridfound == 0) {
                CategoriesFound++;
//                System.out.println("USER ID FOUND ++ CAT --> --> " + CategoriesFound);
                //System.out.println("Not Found User Element");
            }
//            System.out.println("CATEGORY FOUND --> " + CategoriesFound);
            if (CategoriesFound < 5) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "Password must contains 8 characters and   5 categories   Upper Case, Lower Case ,Numbers  and Special Character (@#$%^_+=) ,Password Cannot Contain UserId Elements.");
                Parser.SetField("FormName", "ChangePassword");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");
                return;
            }

            NewPassword = FacilityLogin.encrypt(NewPassword);
            if (pass_history.compareTo("0") != 0) {
                stmt = conn.createStatement();
                Query = " select NewPassword from oe.PasswordHistory where UserId='" + UserId + "' order by CreatedDate desc limit " + pass_history;
                rset = stmt.executeQuery(Query);

                while (rset.next()) {
                    if (rset.getString(1).compareToIgnoreCase(NewPassword) == 0) {
                        AlreadyUsed = true;
                        break;
                    }
                }
                rset.close();
                stmt.close();

                if (AlreadyUsed) {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("Message", "Password Already Used, please do not choose  password from your last passwords");
                    Parser.SetField("FormName", "ChangePassword");
                    Parser.SetField("ActionID", "GetInput");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");
                    return;
                }
            }


            helper.updateUserPassword(request, servletContext, conn, out, NewPassword, UserId);

            helper.savePasswordLogs(request, conn, servletContext, passwordEnc, NewPassword, UserId, facilityIndex);

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "Password has been changed successfully. Please Re-Login");
            Parser.SetField("FormName", "ChangePassword");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");

            HttpSession session = request.getSession(false);
            session.removeAttribute("UserId");
            session.removeAttribute("UserType");
            session.removeAttribute("FacilityIndex");

            session.invalidate();

            ServletContext servletcontext = this.getServletContext();

            if (servletContext.getAttribute("ActiveSessions") != null) {
                Hashtable ht = (Hashtable) servletcontext.getAttribute("ActiveSessions");

                ht.remove(UserId);
                servletContext.setAttribute("ActiveSessions", ht);
            }
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }

    private void saveChangePasswordOLD(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, UtilityHelper helper, int facilityIndex) {
        String CurrPassword = request.getParameter("CurrPassword").trim();
        String NewPassword = request.getParameter("NewPassword").trim();
        String NewRPassword = request.getParameter("NewRPassword").trim();
        String passwordEnc = "";
        Statement stmt = null;
        String Query = "";
        try {
            passwordEnc = FacilityLogin.encrypt(CurrPassword);

            String[] UserDetails = helper.loginUserDetails(request, conn, UserId, servletContext);
            if (!passwordEnc.equals(UserDetails[4])) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserName", UserDetails[0]);
                Parser.SetField("ClientName", UserDetails[3]);
                Parser.SetField("Error", "Current Password Does not Match!!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ChangePasswordInput.html");
                return;
            }

            if (!NewPassword.equals(NewRPassword)) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserName", UserDetails[0]);
                Parser.SetField("ClientName", UserDetails[3]);
                Parser.SetField("Error", "Password Mismatch, Please Enter Again ...!!!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ChangePasswordInput.html");
                return;
            }
            NewPassword = FacilityLogin.encrypt(NewPassword);

            stmt = conn.createStatement();
            Query = "Update oe.sysusers set password = '" + NewPassword + "', PChangeDate = NOW() where ltrim(rtrim(UPPER(userid))) = ltrim(rtrim(UPPER('" + UserId + "')))";
            stmt.execute(Query);

            //helper.updateUserPassword(request, conn, facilityIndex, NewPassword, servletContext);

            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Message", "Password has been changed successfully. Please Re-Login");
            Parser.SetField("FormName", "ChangePassword");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/Message.html");

            HttpSession session = request.getSession(false);
            session.removeAttribute("UserId");
            session.removeAttribute("UserType");
            session.removeAttribute("FacilityIndex");

            session.invalidate();

            ServletContext servletcontext = this.getServletContext();

            if (servletContext.getAttribute("ActiveSessions") != null) {
                Hashtable ht = (Hashtable) servletcontext.getAttribute("ActiveSessions");

                ht.remove(UserId);
                servletContext.setAttribute("ActiveSessions", (Object) ht);
            }

        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }
}
