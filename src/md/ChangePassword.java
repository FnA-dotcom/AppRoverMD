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
import java.sql.SQLException;
import java.util.Hashtable;

@SuppressWarnings("Duplicates")
public class ChangePassword extends HttpServlet {
    private Connection conn = null;
    Integer ScreenIndex = 30;



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
            boolean validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }

            UserId = session.getAttribute("UserId").toString();
            DatabaseName = session.getAttribute("DatabaseName").toString();
            FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
            int UserIndex = Integer.parseInt(session.getAttribute("UserIndex").toString());

            if (UserId.equals("")) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }
            Action = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);

            if(!helper.AuthorizeScreen(request,out,conn,context,UserIndex,this.ScreenIndex)){
//                out.println("You are not Authorized to access this page");
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "You are not Authorized to access this page");
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/Message.html");
                return;
            }

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
                    this.saveChangePassword(request, out, conn, context, UserId, helper, FacilityIndex);
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

    private void saveChangePassword(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, UtilityHelper helper, int facilityIndex) {
        String CurrPassword = request.getParameter("CurrPassword").trim();
        String NewPassword = request.getParameter("NewPassword").trim();
        String NewRPassword = request.getParameter("NewRPassword").trim();
        String passwordEnc = "";
        try {
            passwordEnc = FacilityLogin_old.encrypt(CurrPassword);

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
            NewPassword = FacilityLogin_old.encrypt(NewPassword);
//            helper.updateUserPassword(request, conn, facilityIndex, NewPassword, servletContext);

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
}
