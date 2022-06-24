package md;

import DAL.Payments;
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
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Patient_Visit_Report extends HttpServlet {


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
        Connection conn = null;
        ServletContext context = getServletContext();
        PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        response.setContentType("text/html");
        Services supp = new Services();
        UtilityHelper helper = new UtilityHelper();
        Payments payments = new Payments();
        try {
            HttpSession session = request.getSession(false);
            boolean validSession = helper.checkSession(request, context, session, out);
            if (!validSession) {
                out.flush();
                out.close();
                return;
            }
            String UserId = session.getAttribute("UserId").toString();
            String DatabaseName = session.getAttribute("DatabaseName").toString();
            int FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
            int UserType = Integer.parseInt(session.getAttribute("UserType").toString());
            if (UserId.equals("") || UserId.isEmpty()) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }
            String ActionID = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }

            System.out.println("Inside VisitReport ActionID " + ActionID);
            if (ActionID.equals("GetInput")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "", "Click on the Facility Option", FacilityIndex);
                GetInput(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);
            } else if (ActionID.equals("VisitReport")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "", "Click on the Facility Option", FacilityIndex);
                VisitReport(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper);


            } else {
                helper.deleteUserSession(request, conn, session.getId());
                session.invalidate();
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ErrorMaintenance.html");
            }
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in AddFacility ** (handleRequest)", context, e, "AddFacility", "handleRequest", conn);
            Services.DumException("AddFacility", "Handle Request", request, e, getServletContext());
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
                helper.SendEmailWithAttachment("Error in AddFacility ** (handleRequest -- SqlException)", context, e, "AddFacility", "handleRequest", conn);
                Services.DumException("AddFacility", "Handle Request", request, e, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            }
            out.flush();
            out.close();
        }
    }


    void GetInput(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        Statement stmt1 = null;
        ResultSet rset1 = null;
        String Query1 = "";
//        int PatientCount = 0;
        String FromDate = "";
        String ToDate = "";
        String _FromEndDate = "";
        String _ToEndDate = "";
        StringBuffer CDRList = new StringBuffer();

//        StringBuffer DoctorsList = new StringBuffer();
        StringBuffer DoctorsData = new StringBuffer();
        StringBuffer DoctorNames = new StringBuffer();

        StringBuffer Marketing_Data = new StringBuffer();
        StringBuffer marketing_LABEL = new StringBuffer();

        String PatientsCurrentMonthDaily = "";
        String PatientCountAgeWise = "";
        String CurrentYear = "";
        String DateNow = "";
        String PatientCountMonthly = "";
        String CurrentDate = "";
        int PatientCountCurrentMonth = 0;
        int PatientCountOverAll = 0;
        int PatientCountMale = 0;
        int PatientCountFemale = 0;
        int PatientCountSelfPay = 0;
        int PatientCountInsured = 0;
        int PatientCountWC = 0;
        int PatientCountMVA = 0;
        int PatientCountCOVID = 0;

        int PatientCountToday = 0;


        String TODAY = "";
        int PatientCountOverAll_TODAY = 0;
        int PatientCountInsured_TODAY = 0;
        int PatientCountSelfPay_TODAY = 0;
        int PatientCountWC_TODAY = 0;
        int PatientCountMVA_TODAY = 0;
        int PatientCountCOVID_TODAY = 0;


        String WEEK_START = "";
        String WEEK_END = "";
        int PatientCountOverAll_WEEKLY = 0;
        int PatientCountInsured_WEEKLY = 0;
        int PatientCountSelfPay_WEEKLY = 0;
        int PatientCountWC_WEEKLY = 0;
        int PatientCountMVA_WEEKLY = 0;
        int PatientCountCOVID_WEEKLY = 0;

        String MONTH_START = "";
        String MONTH_END = "";
        int PatientCountOverAll_MONTHLY = 0;
        int PatientCountInsured_MONTHLY = 0;
        int PatientCountSelfPay_MONTHLY = 0;
        int PatientCountWC_MONTHLY = 0;
        int PatientCountMVA_MONTHLY = 0;
        int PatientCountCOVID_MONTHLY = 0;

        String LAST_MONTH_START = "";
        String LAST_MONTH_END = "";
        int PatientCountOverAll_LAST_MONTHLY = 0;

        String PatientsCurrentWeekDaily = "";

        int presentDayNumber = 0;


        try {

            final Parsehtm Parser = new Parsehtm(request);
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/Patient_Visit_Report.html");
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in Patient_Visit_Report ** (ShowReport)", servletContext, e, "Patient_Visit_Report", "ShowReport", conn);
            Services.DumException("ShowReport", "AddFacility ", request, e);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#002");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");

        }
    }


    void VisitReport(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper) throws FileNotFoundException {
        System.out.println("Inside VisitReport");
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";
        String FromDate = "";
        String ToDate = "";
        String _FromEndDate = "";
        String _ToEndDate = "";
        int PatientCountOverAll_MONTHLY = 0;
        String PatientCountMonthly = "";
        StringBuffer visitlist = new StringBuffer();
        String jan, feb, mar, apr, may, jun, jul, aug, sep, oct, nov, dec = "";
//         Stack<String> STACK = new Stack<String>();
        try {

            String Year = request.getParameter("Year");


//    		
//    		 Query = "SELECT YEAR(NOW())";
//             stmt = conn.createStatement();
//             rset = stmt.executeQuery(Query);
//             if (rset.next()) {
//                 CurrentYear = rset.getString(1);
//             }
//             rset.close();
//             stmt.close();

            String[] Months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

            for (int i = 0; i <= 11; i++) {
//                 Query = "Select COUNT(*) from " + Database + ".PatientVisit where  DATE_FORMAT(DateOfService,'%Y-%m') = '" + CurrentYear + "-" + Months[i] + "'";
                Query = "Select COUNT(*) from " + Database + ".PatientVisit a " +
                        "INNER JOIN " + Database + ".PatientReg b ON a.PatientRegId=b.ID " +
                        "where  " +
                        "DATE_FORMAT(DATE_FORMAT(a.DateOfService,'%Y-%m-%d %h:%i:%s'),'%Y-%m') = '" + Year + "-" + Months[i] + "' AND " +
                        "b.status=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    PatientCountMonthly += rset.getString(1) + ",";
//                     STACK.push(PatientCountMonthly);
                }
                rset.close();
                stmt.close();
            }
            if (PatientCountMonthly.endsWith(",")) {
                PatientCountMonthly = PatientCountMonthly.substring(0, PatientCountMonthly.length() - 1);
            }
//             jan = PatientCountMonthly.substring(1);
            System.out.println("PatientCountMonth -> " + PatientCountMonthly.split(","));
            System.out.println("PatientCountMonth -> " + PatientCountMonthly.split(",")[0]);
            jan = PatientCountMonthly.split(",")[0];
            feb = PatientCountMonthly.split(",")[1];
            mar = PatientCountMonthly.split(",")[2];
            apr = PatientCountMonthly.split(",")[3];
            may = PatientCountMonthly.split(",")[4];
            jun = PatientCountMonthly.split(",")[5];
            jul = PatientCountMonthly.split(",")[6];
            aug = PatientCountMonthly.split(",")[7];
            sep = PatientCountMonthly.split(",")[8];
            oct = PatientCountMonthly.split(",")[9];
            nov = PatientCountMonthly.split(",")[10];
            dec = PatientCountMonthly.split(",")[11];

            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("PatientCountMonthly", String.valueOf(PatientCountMonthly));
            Parser.SetField("jan", String.valueOf(jan));
            Parser.SetField("feb", String.valueOf(feb));
            Parser.SetField("mar", String.valueOf(mar));
            Parser.SetField("apr", String.valueOf(apr));
            Parser.SetField("may", String.valueOf(may));
            Parser.SetField("jun", String.valueOf(jun));
            Parser.SetField("jul", String.valueOf(jul));
            Parser.SetField("aug", String.valueOf(aug));
            Parser.SetField("sep", String.valueOf(sep));
            Parser.SetField("oct", String.valueOf(oct));
            Parser.SetField("nov", String.valueOf(nov));
            Parser.SetField("dec", String.valueOf(dec));
            Parser.SetField("SearchedYear", String.valueOf(Year));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/Patient_Visit_Report.html");


        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in Patient_Visit_Report ** (ShowReport)", servletContext, e, "Patient_Visit_Report", "ShowReport", conn);
            Services.DumException("ShowReport", "Patient_Visit_Report ", request, e);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "Patient_Visit_Report");
            Parser.SetField("ActionID", "VisitReport");
            Parser.SetField("Message", "MES#002");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");

        }


//    	GetInput(request, out, conn, servletContext, PatientCountMonthly, PatientCountMonthly, PatientCountOverAll_MONTHLY, helper);
    }
}
