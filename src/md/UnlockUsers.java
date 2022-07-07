//
// Decompiled by Procyon v0.5.36
//

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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@SuppressWarnings("Duplicates")
public class UnlockUsers extends HttpServlet {

    Integer ScreenIndex = 32;

    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        this.handleRequest(request, response);
    }

    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        this.handleRequest(request, response);
    }

    public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String UserId = "";
        int FacilityIndex = 0;
        String DatabaseName = "";
        PrintWriter out = new PrintWriter(response.getOutputStream());
        Services supp = new Services();
        Connection conn = null;

        String ActionID;
        ServletContext context = null;
        context = this.getServletContext();
        UtilityHelper helper = new UtilityHelper();
        int UserIndex = 0;
        int UserType = 0;
        try {
            HttpSession session = request.getSession(false);


            boolean validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }

            if (session.getAttribute("UserId") != null || !session.getAttribute("UserId").equals("")) {
                UserId = session.getAttribute("UserId").toString();
                DatabaseName = session.getAttribute("DatabaseName").toString();
                FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
                UserIndex = Integer.parseInt(session.getAttribute("UserIndex").toString());
                UserType = Integer.parseInt(session.getAttribute("UserType").toString());

                if (UserId.equals("")) {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                    out.flush();
                    out.close();
                    return;
                }
            }

            ActionID = request.getParameter("ActionID").trim();
            conn = Services.getMysqlConn(context);

/*            if (!helper.AuthorizeScreen(request, out, conn, context, UserIndex, this.ScreenIndex)) {
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
            switch (ActionID) {
                case "GetInput":
                    GetInput(request, out, conn, context, UserId, FacilityIndex, UserType, helper);
                    break;
                case "LockUser":
                    LockUser(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                default:
                    out.println("Under Development");
                    break;
            }
        } catch (Exception e) {
            out.println("Exception in main... " + e.getMessage());
            String str = "";
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                str = str + e.getStackTrace()[i] + "<br>";
            }
            out.println(str);
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

    void GetInput(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, int facilityIndex, final int userType, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = null;
        StringBuffer users = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();

        String facilityName = helper.getFacilityName(request, conn, servletContext, facilityIndex);
        try {

            if (userType == 4)
                Query = "SELECT indexptr , username FROM oe.sysusers where clientid = " + facilityIndex + " and " +
                        "enabled='Y' AND status=0";
            else if (UserId.equals("monica"))
                Query = "SELECT indexptr , username FROM oe.sysusers where enabled='Y' AND status=0";
            else
                Query = "SELECT indexptr , username FROM oe.sysusers where usertype IN (7,10,9,12,2,3) AND status = 0";

            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            users.append("<option value='' selected disabled>Select Employee</option>");
            while (rset.next())
                users.append("<option value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
            rset.close();
            stmt.close();

            if (userType == 4)
                Query = "SELECT indexptr,username " +
                        "FROM oe.sysusers where clientid = " + facilityIndex + " and enabled = 'N'";
            else if (UserId.equals("monica"))
                Query = "SELECT indexptr,username FROM oe.sysusers where enabled = 'N'";
            else
                Query = "SELECT indexptr,username " +
                        "FROM oe.sysusers where usertype IN (7,10,9,12,2,3) and enabled = 'N'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                CDRList.append("<tr>\n");
                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left><button type=\"button\" class=\"waves-effect waves-light btn btn-outline btn-rounded btn-info\" onclick=\"Unlock(" + rset.getString(1) + ");\">Unlock</button></td>");
                CDRList.append("</tr>\n");
            }
            rset.close();
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("users", String.valueOf(users));
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/UnlockUsers.html");
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in UnlockUser ** (GetInput^^ " + facilityName + ")", servletContext, e, "UnlockUser", "GetInput", conn);
            Services.DumException("UnlockUser", "GetInput", request, e, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
        }
    }

    void LockUser(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        try {
            String Employee_id = request.getParameter("Employee_id").trim();
            String option = request.getParameter("option").trim();
            String status = request.getParameter("status").trim();

            PreparedStatement MainReceipt = conn.prepareStatement(
                    "UPDATE  oe.sysusers  SET enabled = '" + option + "', status = " + status + " " +
                            "WHERE indexptr=" + Employee_id);
            MainReceipt.executeUpdate();
            MainReceipt.close();

            out.println("1");
        } catch (Exception e) {
            out.println(e.getMessage());
            e.getStackTrace();
        }
    }

}