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

@SuppressWarnings("Duplicates")
public class ForcedPasswordChanged extends HttpServlet {
    Integer ScreenIndex = 30;
    String Query = "";
    Statement stmt = null;
    ResultSet rset = null;
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
        String DatabaseName;
        try {
            HttpSession session = request.getSession(false);
            UtilityHelper helper = new UtilityHelper();

            Action = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);
            if (conn == null) {
                out.println("connection is null");
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            switch (Action) {
                case "GetInput":
                    GetInput(request, out, conn, context, helper);
                    break;
                case "saveChangePassword":
                    this.saveChangePassword(request, out, conn, context, helper);
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

    private void GetInput(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, UtilityHelper helper) {
        try {
//            String[] UserDetails = helper.loginUserDetails(request, conn, UserId, servletContext);
            Parsehtm Parser = new Parsehtm(request);
//            Parser.SetField("UserName", UserDetails[0]);
//            Parser.SetField("ClientName", UserDetails[3]);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ChangePwdForce.html");
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }

    private void saveChangePassword(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, UtilityHelper helper) {
        String CurrPassword = request.getParameter("CurrPassword").trim();
        String NewPassword = request.getParameter("NewPassword").trim();
        String NewRPassword = request.getParameter("NewRPassword").trim();
        String UserId = request.getParameter("userID").trim();
        String passwordEnc = "";
        int facilityIndex = 0;
        stmt = null;
        rset = null;
        Query = "";
        try {
            passwordEnc = FacilityLogin.encrypt(CurrPassword);
            String[] UserDetails = helper.loginUserDetails(request, conn, UserId, servletContext);
            if (!passwordEnc.equals(UserDetails[4])) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserName", UserDetails[0]);
                Parser.SetField("UserId", UserId);
                Parser.SetField("Error", "Current Password Does not Match!!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ChangePwdForce.html");
                return;
            }

            if (!NewPassword.equals(NewRPassword)) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("UserName", UserDetails[0]);
                Parser.SetField("UserId", UserId);
                Parser.SetField("Error", "Password Mismatch, Please Enter Again ...!!!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/ChangePwdForce.html");
                return;
            }
            NewPassword = FacilityLogin.encrypt(NewPassword);

            //helper.updateUserPassword(request, conn, facilityIndex, NewPassword, servletContext);
            stmt = conn.createStatement();
            Query = "Update oe.sysusers set password = '" + NewPassword + "', PChangeDate = NOW(), " +
                    "LoginCount = LoginCount + 1, MaxRetryAllowed = 5, PRetry = 0 " +
                    "where ltrim(rtrim(UPPER(userid))) = ltrim(rtrim(UPPER('" + UserId + "')))";
            stmt.executeUpdate(Query);
            stmt.close();

            //helper.UpdateLoginCount(request, UserId, conn, servletContext);
            Parsehtm Parser = new Parsehtm(request);
//            Parser.SetField("Message", "Password has been changed successfully. Please Re-Login");
//            Parser.SetField("FormName", "ChangePassword");
            Parser.SetField("Error", "Password has been changed successfully. Please Re-Login");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "FacilityLogin.html");

            HttpSession session = request.getSession(false);
            session.removeAttribute("UserId");
            session.removeAttribute("UserType");
            session.removeAttribute("FacilityIndex");

            session.invalidate();

            ServletContext servletcontext = this.getServletContext();

            if (servletContext.getAttribute("ActiveSessions") != null) {
                Hashtable ht = (Hashtable) servletcontext.getAttribute("ActiveSessions");

//                ht.remove(UserId);
                servletContext.setAttribute("ActiveSessions", ht);
            }

        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }
}
