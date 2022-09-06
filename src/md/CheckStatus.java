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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("Duplicates")
public class CheckStatus extends HttpServlet {

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
        Connection conn = null;
        String ActionID;
        Services supp = new Services();

        ServletContext context;
        context = getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        String UserId;
        int FacilityIndex;
        String DatabaseName;
        UtilityHelper helper = new UtilityHelper();

        try {
            HttpSession session = request.getSession(false);

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

            try {
                boolean ValidSession = FacilityLogin.checkSession(out, request, context, response);
                if (!ValidSession) {
                    out.flush();
                    out.close();
                    return;
                }
                if (UserId.equals("")) {
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                    out.flush();
                    out.close();
                    return;
                }
            } catch (Exception excp) {
                conn = null;
                out.println("Exception excp conn: " + excp.getMessage());
            }

            ActionID = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);


            switch (ActionID) {
                case "GetInput":
                    this.GetInput(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
                    break;
                case "GetReport":
                    this.GetReport(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
                    break;
                case "UpdateStatus":
                    this.UpdateStatus(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
                    break;
                default:
                    helper.deleteUserSession(request, conn, session.getId());
                    //Invalidating Session.
                    session.invalidate();
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.SetField("FormName", "ManagementDashboard");
                    Parser.SetField("ActionID", "GetInput");
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
                    break;
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


    private void GetInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, UtilityHelper helper) throws ServletException, IOException {

        ResultSet rset;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();

        StringBuffer CDRList = new StringBuffer();
        try {
            PreparedStatement ps = conn.prepareStatement(
            "SELECT a.Id,IFNULL(a.MRN,0), IFNULL(a.InvoiceNo,'Insurance'), a.Routing, a.Account, a.CheckNo, a.Amount, a.Description,\n" +
                    " b.SettlementStatus , a.CreatedDate , b.Descritpion " +
                    " FROM " + Database + ".CheckInfo a " +
                    " RIGHT JOIN oe.SettlementStatuses b " +
                    " ON a.Status=b.Id " +
                    " where " +
                    "CreatedDate between '" + df.format(currentDate) + " 00:00:00' and '" + df.format(currentDate) + " 23:59:59'"
            );
            rset = ps.executeQuery();
            while (rset.next()) {
                CDRList.append("<tr>\n");
                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(8) + "</td>\n");
                CDRList.append("<td align=left ><span data-toggle=\"tooltip\" title=\""+ rset.getString(11) +"\">" + rset.getString(9) + "</span></td>\n");
                CDRList.append("<td align=left>" + rset.getString(10) + "</td>\n");
                CDRList.append("</tr>\n");
            }
            rset.close();
            ps.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("from", String.valueOf(df.format(currentDate)));
            Parser.SetField("To", String.valueOf(df.format(currentDate)));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/CheckStatusDateWise.html");
        } catch (Exception var11) {
            out.println(var11.getMessage());
            Parsehtm Parser = new Parsehtm(request);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }


    private void GetReport(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, UtilityHelper helper) throws ServletException, IOException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";

        String FromDate = request.getParameter("FromDate").trim();
        String ToDate = request.getParameter("ToDate").trim();
        StringBuffer CDRList = new StringBuffer();

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT a.Id,IFNULL(a.MRN,0), IFNULL(a.InvoiceNo,'Insurance'), a.Routing, a.Account, a.CheckNo, a.Amount, a.Description,\n" +
                            " b.SettlementStatus , a.CreatedDate , b.Descritpion " +
                            " FROM " + Database + ".CheckInfo a " +
                            " RIGHT JOIN oe.SettlementStatuses b " +
                            " ON a.Status=b.Id " +
                            " where " +
                            " CreatedDate between '" + FromDate + " 00:00:00' and '" + ToDate + " 23:59:59'"
            );
            rset = ps.executeQuery();
            while (rset.next()) {
                CDRList.append("<tr>\n");
                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(8) + "</td>\n");
                CDRList.append("<td align=left ><span data-toggle=\"tooltip\" title=\""+ rset.getString(11) +"\">" + rset.getString(9) + "</span></td>\n");
                CDRList.append("<td align=left>" + rset.getString(10) + "</td>\n");
                CDRList.append("</tr>\n");
            }
            rset.close();
            ps.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("UserId", String.valueOf(UserId));
            Parser.SetField("from", String.valueOf(FromDate));
            Parser.SetField("To", String.valueOf(ToDate));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/CheckStatusDateWise.html");
        } catch (Exception var11) {
            out.println(var11.getMessage());
            Parsehtm Parser = new Parsehtm(request);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }

    }

    private void UpdateStatus(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId, UtilityHelper helper) throws ServletException, IOException {
//        out.println("Inside Update Status");

        String status = request.getParameter("status");
        String id = request.getParameter("id");
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";

        try {
            Query = "UPDATE " + Database + ".CheckInfo SET isPaid = '" + status + "' WHERE Id = '" + id + "'";
//            out.println("Inside Update Status Query : " + Query);
            stmt = conn.createStatement();
            stmt.executeUpdate(Query);

            rset.close();
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/CheckStatusDateWise.html");
        } catch (Exception var11) {
            out.println(var11.getMessage());
            Parsehtm Parser = new Parsehtm(request);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }
}