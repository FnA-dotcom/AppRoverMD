package md;

import Handheld.UtilityHelper;
import Parsehtm.Parsehtm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@SuppressWarnings("Duplicates")
public class EPDStatusReport extends HttpServlet {
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = "";
    private Connection conn = null;

    Integer ScreenIndex = 21;


    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        this.handleRequest(request, response);
    }

    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        this.handleRequest(request, response);
    }

    public void handleRequestOLD(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        String Database = "";
        String Query = "";
        int ClientId = 0;
        final String ActionID = request.getParameter("ActionID").trim();
        response.setContentType("text/html");
        final PrintWriter out = new PrintWriter(response.getOutputStream());
        final Services supp = new Services();
        ServletContext context = null;
        context = this.getServletContext();
        conn = Services.getMysqlConn(context);
        String UserId = "";
        try {
            final Cookie[] cookies = request.getCookies();
            for (Cookie cooky : cookies) {
                final String cName = cooky.getName();
                final String cValue = cooky.getValue();
                if (cName.equals("UserId")) {
                    UserId = cValue;
                }
            }
            Query = "Select ClientId from oe.sysusers where ltrim(rtrim(UPPER(UserId))) = ltrim(rtrim(UPPER('" + UserId + "')))";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                ClientId = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select dbname from oe.clients where Id = " + ClientId;
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Database = rset.getString(1);
            }
            rset.close();
            stmt.close();
            UtilityHelper helper = new UtilityHelper();
            if (ActionID.equals("GetInput")) {
                GetInput(request, out, conn, context, UserId, ClientId, helper);
            } else {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
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

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String Action;

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

            if (UserId.equals("")) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }
            Action = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }


            if(!helper.AuthorizeScreen(request,out,conn,context,UserIndex,this.ScreenIndex)){
//                out.println("You are not Authorized to access this page");
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Message", "You are not Authorized to access this page");
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/Message.html");
                return;
            }

            switch (Action) {
                case "GetInput":
                    GetInput(request, out, conn, context, UserId, FacilityIndex, helper);
                    break;
                case "showReport":
                    showReport(request, out, conn, context, UserId, FacilityIndex, helper, DatabaseName);
                    break;
                default:
                    helper.deleteUserSession(request, conn, session.getId());
                    //Invalidating Session.
                    session.invalidate();
                    Parsehtm Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
                    break;
            }
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in EPD Status Report ** (handleRequest)",  context, Ex, "EPDStatusReport", "handleRequest", conn);
            Services.DumException("EPDStatusReport", "Handle Request", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
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
            Parsehtm Parser = new Parsehtm(request);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/EPDStatusInput.html");
        } catch (Exception Ex) {
            helper.SendEmailWithAttachment("Error in EPD Status Report ** (GetInput)",  servletContext, Ex, "EPDStatusReport", "GetInput", conn);
            Services.DumException("EPDStatusReport", "GetInput", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            try {
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    private void showReport(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext context, String userId, int facilityIndex, UtilityHelper helper, String databaseName) {
        stmt = null;
        rset = null;
        Query = "";
        StringBuilder StatusReport = new StringBuilder();
        String FromDate = request.getParameter("FromDate").trim();
        String ToDate = request.getParameter("ToDate").trim();
        String Status = "";

        try {
            Query = "SELECT a.Id,DATE_FORMAT(a.requestdate,'%d-%b-%Y %h:%i:%s') AS RequestDate,a.mrn,a.ResponseCode, \n" +
                    "CONCAT(b.FirstName, ' ', b.LastName , ' ', b.MiddleInitial) AS PatientName, " +
                    "DATE_FORMAT(a.posttime,'%d-%b-%Y %h:%i:%s') AS ReceivedDate," +
                    "CASE " +
                    "WHEN a.ResponseCode = 1 THEN 'SENT TO EPD' \n" +
                    "WHEN a.ResponseCode = 0 THEN 'Pending' \n" +
                    "ELSE 'No Result' \n" +
                    "END,b.Id \n" +
                    " FROM oe.request a \n" +
                    " STRAIGHT_JOIN " + databaseName + ".PatientReg b ON a.mrn = b.MRN \n" +
                    " WHERE a.ClientIndex = " + facilityIndex + " AND a.requestdate BETWEEN '" + FromDate + " 00:00:00' AND '" + ToDate + " 23:59:59' \n" +
                    " ORDER BY a.requestdate DESC";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                StatusReport.append("<tr>");
                StatusReport.append("<td width=01%>" + rset.getString(3) + "</td>");//MRN
                StatusReport.append("<td width=01%>" + rset.getString(5) + "</td>");//NAME
                StatusReport.append("<td width=05%>" + rset.getString(2) + "</td>");//SentDate
                StatusReport.append("<td width=01%>" + rset.getString(6) + "</td>");//ReceivedDate
                StatusReport.append("<td width=01%>" + rset.getString(7) + "</td>");//Status
                StatusReport.append("<td width=05%><button id=sendToEPD title=\"Sent To EPD\" onclick=\"sendEPD(this.value)\" value=" + rset.getInt(8) + "  class=\"btn btn-danger btn-md\"> <font color = \"FFFFFF\"> <i class=\"fa fa-plus\" ></i> [SEND] </font></button></td>");
                StatusReport.append("</tr>");
            }
            rset.close();
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("StatusReport", StatusReport.toString());
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Reports/EPDStatusInput.html");

        } catch (Exception Ex) {
            //helper.SendEmail("Main Section", "Error in Transaction Report", "Message Body");
            helper.SendEmailWithAttachment("Error in EPD Status Report ** (showReport)", context, Ex, "EPDStatusReport", "showReport", conn);
            Services.DumException("EPDStatusReport", "showReport", request, Ex, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "EPDStatusReport");
            Parser.SetField("ActionID", "GetInput");
            try {
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
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
}
