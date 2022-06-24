//
// Decompiled by Procyon v0.5.36
//

package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@SuppressWarnings("Duplicates")
public class RequestReport13MARCH2021 extends HttpServlet {
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = "";
    private Connection conn = null;

    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        this.handleRequest(request, response);
    }

    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        this.handleRequest(request, response);
    }

    public void handleRequestold(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        ResultSet rset = null;
        Statement stmt = null;
        String UserId = "";
        String Zone = "";
        String Passwd = "";
        String Database = "";
        String Query = "";
        int ClientId = 0;
        final String ActionID = request.getParameter("ActionID").trim();
        response.setContentType("text/html");
        final PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        final Services supp = new Services();
//    conn = Services.getMysqlConn();
        ServletContext context = null;
        context = this.getServletContext();
        conn = Services.getMysqlConn(context);
        try {
            final Cookie[] cookies = request.getCookies();
            UserId = (Zone = (Passwd = ""));
            String UserName = "";
            final int checkCookie = 0;
            for (int coky = 0; coky < cookies.length; ++coky) {
                final String cName = cookies[coky].getName();
                final String cValue = cookies[coky].getValue();
                if (cName.equals("UserId")) {
                    UserId = cValue;
                }
                if (cName.equals("username")) {
                    UserName = cValue;
                }
            }
            Query = "Select ClientId from oe.sysusers where ltrim(rtrim(UPPER(UserId))) = ltrim(rtrim(UPPER('" + UserId + "')))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                ClientId = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select dbname from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                Database = rset.getString(1);
            }
            rset.close();
            stmt.close();

            if (ActionID.equals("GetReport")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get request table report", "Open oe request Report Screen", ClientId);
                this.GetReport(request, out, conn, context, UserId, Database, ClientId);
            } else {
                out.println("Under Development");
            }
            try {
                conn.close();
            } catch (Exception ex) {
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }

    public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String UserId;
        int FacilityIndex;
        String DatabaseName;
        PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        Services supp = new Services();

        String ActionID;
        ServletContext context = null;
        context = this.getServletContext();
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

            if (UserId.equals("")) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }

            ActionID = request.getParameter("ActionID").trim();
            conn = Services.getMysqlConn(context);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }

            if (ActionID.equals("GetReport")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get request table report", "Open oe request Report Screen", FacilityIndex);
                this.GetReport(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else {
                helper.deleteUserSession(request, conn, session.getId());
                //Invalidating Session.
                session.invalidate();
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
            }
        } catch (Exception e) {
            out.println("Exception in main... " + e.getMessage());
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


    void GetReport(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        SupportiveMethods suppMethods = new SupportiveMethods();
        StringBuffer LeftSideBarMenu = new StringBuffer();
        StringBuffer Header = new StringBuffer();
        StringBuffer Footer = new StringBuffer();
        StringBuffer CDRList = new StringBuffer();
        try {


            CDRList.append("<div class=\"table-responsive\">");
            CDRList.append("<table class=\"table table-striped mb-0\" style=\"width:100%\">");
            CDRList.append("<thead style=\"color:black;\">");
            CDRList.append("<tr>");
            CDRList.append("<th >Id</th>");
            CDRList.append("<th >ClientIndex</th>");
            CDRList.append("<th >MSG</th>");
            CDRList.append("<th >RequestDate</th>");
            CDRList.append("<th >PostTime</th>");
            CDRList.append("<th >Status</th>");
            CDRList.append("<th >RequestType</th>");
            CDRList.append("<th >MRN</th>");
            CDRList.append("<th >Flag</th>");
            CDRList.append("<th >Response</th>");
            CDRList.append("<th >ResponseCode</th>");
            CDRList.append("<th >MSCID</th>");
            CDRList.append("</tr>");
            CDRList.append("<tbody style=\"color:black;\">");

            Query = "Select Id, msg, requestdate, posttime, status, RequestType, mrn, flag, ClientIndex,  Response, ResponseCode, MSCID \n" +
                    " from oe.request order by Id desc limit 30;";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                if (rset.getString(10) == null) {
                    CDRList.append("<tr style=\"background-color: #FF7C60;\"><td align=left>" + rset.getInt(1) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(9) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(8) + "</td>\n");

                    CDRList.append("<td align=left>" + rset.getString(10) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(11) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(12) + "</td>\n");
                    CDRList.append("</tr>");
                } else {
                    CDRList.append("<tr style=\"background-color: 60FF66;\"><td align=left>" + rset.getInt(1) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(9) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(8) + "</td>\n");

                    CDRList.append("<td align=left>" + rset.getString(10) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(11) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(12) + "</td>\n");
                    CDRList.append("</tr>");
                }
            }
            rset.close();
            stmt.close();

            CDRList.append("</tbody>");
            CDRList.append("</table>");
            CDRList.append("</div>");

            Header = suppMethods.Header(request, out, conn, servletContext, UserId, Database, ClientId);
            LeftSideBarMenu = suppMethods.LeftSideBarMenu(request, out, conn, servletContext, UserId, Database, ClientId);
            Footer = suppMethods.Footer(request, out, conn, servletContext, UserId, Database, ClientId);
            conn.close();
            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/RequestTableReport.html");
        } catch (Exception var11) {
            out.println(var11.getMessage());
        }
    }

}


