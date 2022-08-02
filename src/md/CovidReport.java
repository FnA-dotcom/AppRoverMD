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
public class CovidReport extends HttpServlet {
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
        System.out.println("in handheld request ");
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

                FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());


                if (UserId.equals("")) {
                    Parsehtm parsehtm = new Parsehtm(request);
                    parsehtm.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                    out.flush();
                    out.close();
                    return;
                }
            }
            System.out.println("geting Action ID ");
            ActionID = request.getParameter("ActionID");

            System.out.println(" Action ID " + ActionID);
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
            helper.SendEmailWithAttachment("Error in CovidReport ** (handleRequest)", context, e, "CovidReport", "handleRequest", conn);
            Services.DumException("CovidReport", "Handle Request", request, e, getServletContext());
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
                helper.SendEmailWithAttachment("Error in CovidReport ** (handleRequest -- SqlException)", context, e, "CovidReport", "handleRequest", conn);
                Services.DumException("CovidReport", "Handle Request", request, e, getServletContext());
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
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String today = formatter.format(date);
        StringBuffer CDRList = new StringBuffer();
//        System.out.println("ClientId" + ClientId);
        try {
            if (ClientId == 39 || ClientId == 40) {

                System.out.println("ClientId" + ClientId);
                Query = " SELECT "
                        + "	ifnull(a.name,''),ifnull(a.CLIA,''),ifnull(a.name,''),ifnull(a.Address,''),ifnull(a.City,''),ifnull(a.ZipCode,''),ifnull(a.State,'')," +
                        " CASE " +
                        "when b.CovidStatus = 0 then 'NEGATIVE' " +
                        "when b.CovidStatus = 1 then 'POSITIVE' " +
                        "else 'NO RESULT' END " +
                        ",IFNULL(DATE_FORMAT(b.CovidTestDate, '%m/%d/%Y'),''),IFNULL(DATE_FORMAT(b.CovidtestDate, '%m/%d/%Y'),''),ifnull(c.FirstName,''),ifnull(c.MiddleInitial,''), " +
                        "ifnull(c.LastName,''),IFNULL(DATE_FORMAT(c.DOB, '%m/%d/%Y'),''),ifnull(c.Age,''),ifnull(c.Gender,''), " +
                        "CASE " +
                        "when d.Race = 1 then 'African American' " +
                        "when d.Race = 2 then 'American Indian or Alaska Native' " +
                        "when d.Race = 3 then 'Asian' " +
                        "when d.Race = 4 then 'Native Hawaiian or Other Pacific Islander' " +
                        "when d.Race = 5 then 'White' " +
                        "else 'Other' END" +
                        ",CASE " +
                        "when c.Ethnicity = 1 then 'Hispanic or Latino' " +
                        "when c.Ethnicity = 2 then 'Non Hispanic or Latino' " +
                        "else 'Others' END" +
                        ",ifnull(c.PhNumber,''),ifnull(c.City,''),ifnull(c.State,''),ifnull(c.ZipCode,''),ifnull(c.Country,''),ifnull(a.name,'')," +
                        "ifnull(a.Address,''),ifnull(a.City,''),ifnull(a.State,''),ifnull(a.ZipCode,''),IFNULL(DATE_FORMAT(b.CovidTestDate, '%m/%d/%Y'),'')" +
                        ",IFNULL(DATE_FORMAT(b.CovidTestDate, '%m/%d/%Y'),''),ifnull(c.MRN,'')"
                        + "  FROM " + Database + ".PatientReg c "
                        + "INNER JOIN " + Database + ".Patient_AdditionalInfo b ON c.ID = b.PatientRegId "
                        + "INNER JOIN " + Database + ".PatientReg_Details d ON c.ID = d.PatientRegId "
                        + "INNER JOIN oe.clients a ON c.ClientIndex = a.Id "
                        + "where c.Status=0 ORDER BY c.CreatedDate DESC";

//                System.out.println("Query" + Query);
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);


                while (rset.next()) {

                    CDRList.append("<tr>");

                    CDRList.append("<td align=left>" + rset.getString(1) + "</td>");
                    CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(7) + "</td>");
                    //empty
                    CDRList.append("<td align=left> Empty </td>\n");
                    CDRList.append("<td align=left> Empty </td>\n");
                    CDRList.append("<td align=left> Empty </td>\n");
                    CDRList.append("<td align=left> Empty </td>\n");

                    CDRList.append("<td align=left>" + rset.getString(8) + "</td>\n");
                    //empty
                    CDRList.append("<td align=left> Empty </td>\n");
                    CDRList.append("<td align=left> Empty </td>\n");

                    CDRList.append("<td align=left>" + rset.getString(9) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(10) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(11) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(12) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(13) + "</td>");
                    CDRList.append("<td align=left>" + rset.getString(14) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(15) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(16) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(17) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(18) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(19) + "</td>");
                    //empty
                    CDRList.append("<td align=left> Empty </td>\n");

                    CDRList.append("<td align=left>" + rset.getString(20) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(21) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(22) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(23) + "</td>\n");
                    //empty
                    CDRList.append("<td align=left> Empty </td>\n");

                    CDRList.append("<td align=left>" + rset.getString(24) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(25) + "</td>");
                    CDRList.append("<td align=left>" + rset.getString(26) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(27) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(28) + "</td>\n");
                    //empty
                    CDRList.append("<td align=left> Empty </td>\n");
                    CDRList.append("<td align=left> Empty </td>\n");
                    CDRList.append("<td align=left> Empty </td>\n");
                    CDRList.append("<td align=left> Empty </td>\n");
                    CDRList.append("<td align=left> Empty </td>\n");
                    CDRList.append("<td align=left> Empty </td>\n");
                    CDRList.append("<td align=left> Empty </td>\n");
                    CDRList.append("<td align=left> Empty </td>\n");
                    CDRList.append("<td align=left> Empty </td>\n");
                    CDRList.append("<td align=left> Empty </td>\n");

                    CDRList.append("<td align=left>" + rset.getString(29) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(30) + "</td>\n");
                    CDRList.append("<td align=left>" + rset.getString(31) + "</td>");
                    //empty
                    CDRList.append("<td align=left> Empty </td>\n");
                    CDRList.append("<td align=left> Empty </td>\n");
                    CDRList.append("<td align=left> Empty </td>\n");
                    CDRList.append("<td align=left> Empty </td>\n");
                    CDRList.append("<td align=left> Empty </td>\n");
                    CDRList.append("<td align=left> Empty </td>\n");
                    CDRList.append("<td align=left> Empty </td>\n");

                    CDRList.append("</tr>");

                }
//                System.out.println("Query----------->   " + Query);
                rset.close();
                stmt.close();
//                System.out.println("Query------->  " + Query);

                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("CDRList", String.valueOf(CDRList));
                Parser.SetField("today", String.valueOf(today));
                Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/CovidReport.html");
            }
        } catch (Exception e) {
            e.printStackTrace();
            helper.SendEmailWithAttachment("Error in CovidReport ** (GetInput)", servletContext, e, "CovidReport", "GetInput", conn);
            Services.DumException("CovidReport", "GetInput", request, e, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }


    void GetFilterData(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper, String locationArray, int isLocationAdmin) throws FileNotFoundException {
        System.out.println("in GetFilterData");
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        StringBuffer CDRList = new StringBuffer();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String today = formatter.format(date);


        String FromDate = request.getParameter("FromDate").trim();
        System.out.println("in FromDate " + FromDate);
        String ToDate = request.getParameter("ToDate").trim();
        System.out.println("in ToDate" + ToDate);


        try {
            Query = " SELECT "
                    + "	ifnull(a.name,''),ifnull(a.CLIA,''),ifnull(a.name,''),ifnull(a.Address,''),ifnull(a.City,''),ifnull(a.ZipCode,''),ifnull(a.State,'')," +
                    " CASE " +
                    "when b.CovidStatus = 0 then 'NEGATIVE' " +
                    "when b.CovidStatus = 1 then 'POSITIVE' " +
                    "when b.CovidStatus = -1 then 'SUSPECTED' " +
                    "else 'UNEXAMINED' END " +
                    ",IFNULL(DATE_FORMAT(b.CovidTestDate, '%m/%d/%Y'),''),IFNULL(DATE_FORMAT(b.CovidtestDate, '%m/%d/%Y'),''),ifnull(c.FirstName,''),ifnull(c.MiddleInitial,''), " +
                    "ifnull(c.LastName,''),IFNULL(DATE_FORMAT(c.DOB, '%m/%d/%Y'),''),ifnull(c.Age,''),ifnull(c.Gender,''), " +
                    "CASE " +
                    "when d.Race = 1 then 'African American' " +
                    "when d.Race = 2 then 'American Indian or Alaska Native' " +
                    "when d.Race = 3 then 'Asian' " +
                    "when d.Race = 4 then 'Native Hawaiian or Other Pacific Islander' " +
                    "when d.Race = 5 then 'White' " +
                    "else 'Other' END" +
                    ",CASE " +
                    "when c.Ethnicity = 1 then 'Hispanic or Latino' " +
                    "when c.Ethnicity = 2 then 'Non Hispanic or Latino' " +
                    "else 'Others' END" +
                    ",ifnull(c.PhNumber,''),ifnull(c.City,''),ifnull(c.State,''),ifnull(c.ZipCode,''),ifnull(c.Country,''),ifnull(a.name,'')," +
                    "ifnull(a.Address,''),ifnull(a.City,''),ifnull(a.State,''),ifnull(a.ZipCode,''),IFNULL(DATE_FORMAT(b.CovidTestDate, '%m/%d/%Y'),'')" +
                    ",IFNULL(DATE_FORMAT(b.CovidTestDate, '%m/%d/%Y'),''),ifnull(c.MRN,'')"
                    + "  FROM " + Database + ".PatientReg c "
                    + "INNER JOIN " + Database + ".Patient_AdditionalInfo b ON c.ID = b.PatientRegId "
                    + "INNER JOIN " + Database + ".PatientReg_Details d ON c.ID = d.PatientRegId "
                    + "INNER JOIN oe.clients a ON c.ClientIndex = a.Id "
                    + "where c.Status=0 AND b.CovidTestDate between '" + FromDate + "' AND '" + ToDate + "' ORDER BY c.CreatedDate DESC";

//            System.out.println("Query" + Query);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);


            while (rset.next()) {

                CDRList.append("<tr>");

                CDRList.append("<td align=left>" + rset.getString(1) + "</td>");
                CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(7) + "</td>");
                //empty
                CDRList.append("<td align=left> Empty </td>\n");
                CDRList.append("<td align=left> Empty </td>\n");
                CDRList.append("<td align=left> Empty </td>\n");
                CDRList.append("<td align=left> Empty </td>\n");

                CDRList.append("<td align=left>" + rset.getString(8) + "</td>\n");
                //empty
                CDRList.append("<td align=left> Empty </td>\n");
                CDRList.append("<td align=left> Empty </td>\n");

                CDRList.append("<td align=left>" + rset.getString(9) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(10) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(11) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(12) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(13) + "</td>");
                CDRList.append("<td align=left>" + rset.getString(14) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(15) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(16) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(17) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(18) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(19) + "</td>");
                //empty
                CDRList.append("<td align=left> Empty </td>\n");

                CDRList.append("<td align=left>" + rset.getString(20) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(21) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(22) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(23) + "</td>\n");
                //empty
                CDRList.append("<td align=left> Empty </td>\n");

                CDRList.append("<td align=left>" + rset.getString(24) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(25) + "</td>");
                CDRList.append("<td align=left>" + rset.getString(26) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(27) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(28) + "</td>\n");
                //empty
                CDRList.append("<td align=left> Empty </td>\n");
                CDRList.append("<td align=left> Empty </td>\n");
                CDRList.append("<td align=left> Empty </td>\n");
                CDRList.append("<td align=left> Empty </td>\n");
                CDRList.append("<td align=left> Empty </td>\n");
                CDRList.append("<td align=left> Empty </td>\n");
                CDRList.append("<td align=left> Empty </td>\n");
                CDRList.append("<td align=left> Empty </td>\n");
                CDRList.append("<td align=left> Empty </td>\n");
                CDRList.append("<td align=left> Empty </td>\n");

                CDRList.append("<td align=left>" + rset.getString(29) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(30) + "</td>\n");
                CDRList.append("<td align=left>" + rset.getString(31) + "</td>");
                //empty
                CDRList.append("<td align=left> Empty </td>\n");
                CDRList.append("<td align=left> Empty </td>\n");
                CDRList.append("<td align=left> Empty </td>\n");
                CDRList.append("<td align=left> Empty </td>\n");
                CDRList.append("<td align=left> Empty </td>\n");
                CDRList.append("<td align=left> Empty </td>\n");
                CDRList.append("<td align=left> Empty </td>\n");
                CDRList.append("</tr>");
            }
            rset.close();
            stmt.close();
//            System.out.println("Query after------>    " + Query);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("searchdatefrom", String.valueOf(FromDate));
            Parser.SetField("searchdateto", String.valueOf(ToDate));
            Parser.SetField("today", String.valueOf(today));

            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/CovidReport.html");
        } catch (Exception e) {
            e.printStackTrace();
            helper.SendEmailWithAttachment("Error in CovidReport ** (GetFilterData)", servletContext, e, "CovidReport", "GetFilterData", conn);
            Services.DumException("CovidReport", "getReportInput", request, e, getServletContext());
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetFilterData");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

}