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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("Duplicates")
public class LabReportsManagement extends HttpServlet {
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handleRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handleRequest(request, response);
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ActionID = "";
        String UserId = "";
        String DatabaseName = "";
        String DirectoryName = "";
        HttpSession session = null;
        Connection conn = null;
        boolean validSession = false;
        int FacilityIndex = 0;
        int isLocationAdmin = 0;
        ServletContext context = getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        UtilityHelper helper = new UtilityHelper();
        Services supp = new Services();
        String locationArray = "";

        try {
            Parsehtm Parser;
            if (request.getParameter("ActionID") == null) {
                ActionID = "Nosession";
            } else {
                session = request.getSession(false);
                validSession = helper.checkSession(request, context, session, out);
                if (!validSession) {
                    out.flush();
                    out.close();
                    return;
                }
                UserId = session.getAttribute("UserId").toString();
                DatabaseName = session.getAttribute("DatabaseName").toString();
                locationArray = session.getAttribute("LocationArray").toString();
                DirectoryName = session.getAttribute("DirectoryName").toString();
                FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
                isLocationAdmin = Integer.parseInt(session.getAttribute("isLocationAdmin").toString());

                if (UserId.equals("")) {
                    Parsehtm parsehtm = new Parsehtm(request);
                    parsehtm.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                    out.flush();
                    out.close();
                    return;
                }
            }

            ActionID = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);
            if (conn == null) {
                Parsehtm parsehtm = new Parsehtm(request);
                parsehtm.SetField("Error", "Unable to connect. Our team is looking into it!");
                parsehtm.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            switch (ActionID) {
                case "GetInput":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "View getPatLogsReportInput Option", "Click on getPatLogsReportInput", FacilityIndex);
                    GetInput(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper, locationArray, isLocationAdmin);
                    break;
                case "GetFilterData":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "View getPatLogsReportInput Option", "Click on getPatLogsReportInput", FacilityIndex);
                    GetFilterData(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper, locationArray, isLocationAdmin);
                    break;
                default: {
                    helper.deleteUserSession(request, conn, session.getId());
                    session.invalidate();
                    Parser = new Parsehtm(request);
                    Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
                    break;
                }
            }
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in LabReportsManagement ** (handleRequest)", context, e, "LabReportsManagement", "handleRequest", conn);
            Services.DumException("LabReportsManagement", "Handle Request", request, e, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            out.flush();
            out.close();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                helper.SendEmailWithAttachment("Error in LabReportsManagement ** (handleRequest -- SqlException)", context, e, "LabReportsManagement", "handleRequest", conn);
                Services.DumException("LabReportsManagement", "Handle Request", request, e, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            }
            out.flush();
            out.close();
        }
    }

    void GetInput(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper, String locationArray, int isLocationAdmin) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String Query2 = "";
        StringBuffer Location = new StringBuffer();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String today = formatter.format(date);
        StringBuffer TestWise = new StringBuffer();
        try {

            Query = "Select Id, Location from roverlab.Locations";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            Location.append("<option value=''>Select Location</option>");
            Location.append("<option value=''>All Locations</option>");
            while (rset.next())
                Location.append("<option value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
            rset.close();
            stmt.close();


            Query2 = "Select Id,TestName from roverlab.ListofTests";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query2);
            TestWise.append("<option value=''>Select Test Type</option>");
            TestWise.append("<option value=''>All Tests</option>");
            while (rset.next())
                TestWise.append("<option value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
            rset.close();
            stmt.close();

            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Location", String.valueOf(Location));
//            Parser.SetField("Result", String.valueOf(Location));
            Parser.SetField("TestWise", String.valueOf(TestWise));
            Parser.SetField("searchdatefrom", String.valueOf(today));
            Parser.SetField("searchdateto", String.valueOf(today));
            Parser.SetField("today", String.valueOf(today));

            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/LabReportsManagement.html");
        } catch (Exception e) {
            e.printStackTrace();
            helper.SendEmailWithAttachment("Error in LabReportsManagement ** (getReportInput)", servletContext, e, "LabReportsManagement", "getReportInput", conn);
            Services.DumException("LabReportsManagement", "getReportInput", request, e, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }


    void GetFilterData(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper, String locationArray, int isLocationAdmin) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        System.out.println("in the function");
        String Query2 = "";
        StringBuffer CDRList = new StringBuffer();
        int SNo = 1;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String today = formatter.format(date);
        System.out.println("FromDate" + request.getParameter("FromDate").trim());
        System.out.println("Todate" + request.getParameter("ToDate").trim());
        String FromDate = request.getParameter("FromDate").trim();
        String ToDate = request.getParameter("ToDate").trim();
        StringBuffer LocationList = new StringBuffer();
        StringBuffer TestWiseList = new StringBuffer();

        String Location;
        String Result;
        String TestWise;
        if (request.getParameter("Location") == null) {
            Location = "";
        } else {
            Location = request.getParameter("Location");
        }

        if (request.getParameter("Result") == null) {
            Result = "";
        } else {
            Result = request.getParameter("Result");
        }

        if (request.getParameter("TestWise") == null) {
            TestWise = "";
        } else {
            TestWise = request.getParameter("TestWise");
        }

        System.out.println("Location" + Location);
        System.out.println("Result" + Result);
        System.out.println("TestWise" + TestWise);

        String LocationFilter;
        String ResultFilter;
        String TestFilter;


        if (Location == "" || Location == null) {
            LocationFilter = "";
        } else {

            LocationFilter = " And e.Id =" + Location + "";
        }


        if (Result == "" || Result == null) {
            ResultFilter = "";
        } else {

            ResultFilter = " AND c.TestStatus =" + Result + "";
        }


        if (TestWise == "" || TestWise == null) {
            TestFilter = "";
        } else {

            TestFilter = " AND d.Id =" + TestWise + "";
        }


        try {


            Query = "Select Id, Location from roverlab.Locations";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            LocationList.append("<option value=''>Select Location</option>");
            LocationList.append("<option value=''>All Locations</option>");
            while (rset.next())
                LocationList.append("<option value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
            rset.close();
            stmt.close();


            Query2 = "Select Id, TestName from roverlab.ListofTests";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query2);
            TestWiseList.append("<option value=''>Select Test Type</option>");
            TestWiseList.append("<option value=''>All Tests</option>");
            while (rset.next())
                TestWiseList.append("<option value=\"" + rset.getString(1) + "\">" + rset.getString(2) + "</option>");
            rset.close();
            stmt.close();


            Query = " SELECT\r\n"
                    + "	CONCAT(IFNULL(a.FirstName,''), ' ', IFNULL(a.MiddleInitial,''), ' ', IFNULL(a.LastName,'')),IFNULL(a.MRN, ''),IFNULL(DATE_FORMAT(b.OrderDate, '%m/%d/%Y'),''),IFNULL(b.Id, ''),"
                    + "CASE"
                    + " WHEN c.TestStatus = 1 THEN 'BROKEN' "
                    + "WHEN c.TestStatus = 2 THEN 'NEGATIVE' "
                    + "WHEN c.TestStatus = 3 THEN 'POSITIVE' "
                    + "WHEN c.TestStatus = 4 THEN 'REJECTED' "
                    + "WHEN c.TestStatus = 5 THEN 'LOST' "
                    + "WHEN c.TestStatus = 6 THEN 'UNCONCLUSIVE' "
                    + "ELSE 'No Result' END"
                    + ",IFNULL(e.Location, ''),IFNULL(d.TestName, '')"
                    + "  FROM roverlab.PatientReg a "
                    + "INNER JOIN roverlab.TestOrder b ON a.ID = b.PatRegIdx "
                    + "INNER JOIN roverlab.Tests c ON b.Id = c.OrderId "
                    + "INNER JOIN roverlab.ListofTests d ON c.TestIdx = d.Id "
                    + "INNER JOIN roverlab.Locations e ON a.TestingLocation = e.Id "
                    + "where a.Status=0 AND b.OrderDate BETWEEN '" + FromDate + "' AND '" + ToDate + "'" + LocationFilter + ResultFilter + TestFilter + "";

            System.out.println("Query" + Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);


            while (rset.next()) {

                CDRList.append("<tr>");
                CDRList.append("<td align=left>" + SNo + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(1) + "</td>");
                CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");
                CDRList.append("<td align=left><span class=\"badge badge-success\">" + rset.getString(5) + "</span></td>\n");
                CDRList.append("</tr>");
                SNo++;
            }
            rset.close();
            stmt.close();
            System.out.println("Query after" + Query);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("Location", String.valueOf(LocationList));
            Parser.SetField("CDRList", String.valueOf(CDRList));
//          Parser.SetField("Result", String.valueOf(Location));
            Parser.SetField("TestWise", String.valueOf(TestWiseList));
            Parser.SetField("searchdatefrom", String.valueOf(FromDate));
            Parser.SetField("searchdateto", String.valueOf(ToDate));

            Parser.SetField("Selectedlocation", String.valueOf(Location));
            Parser.SetField("SelectedResult", String.valueOf(Result));
            Parser.SetField("SelectedTestWise", String.valueOf(TestWise));


            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/LabReportsManagement.html");
        } catch (Exception e) {
            e.printStackTrace();
            helper.SendEmailWithAttachment("Error in LabReportsManagement ** (GetFilterData)", servletContext, e, "LabReportsManagement", "GetFilterData", conn);
            Services.DumException("LabReportsManagement", "getReportInput", request, e, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetFilterData");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }
}
