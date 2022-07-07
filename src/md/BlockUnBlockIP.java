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
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@SuppressWarnings("Duplicates")
public class BlockUnBlockIP extends HttpServlet {

    Integer ScreenIndex = 33;
//    private Connection conn = null;

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
//                out.println("You are not Authorized to access this page");
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "You are not Authorized to access this page");
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/Message.html");
//                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/Error404.html");
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
                    GetInput(request, out, conn, context, UserId, DatabaseName, FacilityIndex, UserType);
                    break;
                case "BlockIP":
                    BlockIP(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
                    break;
                case "UnblockIP":
                    UnblockIP(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
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

    void GetInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int facilityIdx, int userType) {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = null;

        StringBuffer CDRList = new StringBuffer();


        try {
            if (userType == 4)
                Query = "SELECT ID,IP FROM oe.BLOCKED_IPS where status = 1 AND facilityIdx = " + facilityIdx;
            else
                Query = "SELECT ID,IP FROM oe.BLOCKED_IPS where status = 1 ";


            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                CDRList.append("<tr>\n");
                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left><button type=\"button\" class=\"waves-effect waves-light btn btn-outline btn-rounded btn-info\" onclick=\"UnblockIP(" + rset.getString(1) + ");\">Unblock IP</button></td>");
                CDRList.append("</tr>\n");
            }
            rset.close();
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/BlockUnBlockIP.html");
        } catch (Exception e) {
            e.getStackTrace();
        }
    }


    void BlockIP(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int facilityIdx) {
        try {
            String IP = request.getParameter("IP").trim();
            Statement stmt = null;
            ResultSet rset = null;
            String Query = null;


            int null_data_found = 0;


            Query = "SELECT COUNT(*) FROM oe.BLOCKED_IPS where IP='" + IP + "'";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);

            if (rset.next()) {
                null_data_found = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            if (null_data_found == 0) {
                PreparedStatement MainReceipt = conn.prepareStatement(
                        "INSERT INTO oe.BLOCKED_IPS (IP,Status,FacilityIdx,CreatedBy,CreatedDate) " +
                                "VALUE(?,'1',?,?,NOW())");

                MainReceipt.setString(1, IP);
                MainReceipt.setInt(2, facilityIdx);
                MainReceipt.setString(3, UserId);
                MainReceipt.executeUpdate();
                MainReceipt.close();
                out.println("1");
            } else {
                out.println("0~IP exists");
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    void UnblockIP(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        try {
            String ID = request.getParameter("ID").trim();

            PreparedStatement MainReceipt = conn.prepareStatement("UPDATE  oe.BLOCKED_IPS  SET Status = '0' WHERE ID = '" + ID + "'");
            MainReceipt.executeUpdate();
            MainReceipt.close();

            out.println("1");
        } catch (Exception e) {
            out.println(e.getMessage());
            e.getStackTrace();
        }
    }

}