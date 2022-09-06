

package md;

import DAL.TwilioSMSConfiguration;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

//import com.itextpdf.text.pdf.PdfWriter;
@SuppressWarnings("Duplicates")
public class SMS_Report extends HttpServlet {
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rset = null;
    private String Query = "";

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
        String ActionID = "";

        ServletContext context;
        context = getServletContext();
        PrintWriter out = new PrintWriter(response.getOutputStream());
        response.setContentType("text/html");
        String UserId;
        int FacilityIndex;
        int UserIndex;
        String DatabaseName;
        UtilityHelper helper = new UtilityHelper();
        Services supp = new Services();
        TwilioSMSConfiguration smsConfiguration = new TwilioSMSConfiguration();
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
            UserIndex = Integer.parseInt(session.getAttribute("UserIndex").toString());

            if (UserId.equals("")) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/SessionTimeOut.html");
                out.flush();
                out.close();
                return;
            }
            ActionID = request.getParameter("ActionID");
            conn = Services.GetConnection(context, 1);
            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            switch (ActionID) {
                case "GetInput":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "SMS_Report", "GetInput Function", FacilityIndex);
                    this.GetInput(request, out, conn, context, UserId, DatabaseName, FacilityIndex, helper, smsConfiguration, UserIndex);
                    break;

                case "GetReport":
                    supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "Get Report SMS ", "Get Report SMS", FacilityIndex);
                    GetReport(request, out, conn, context, UserIndex, DatabaseName, FacilityIndex, smsConfiguration);
                    break;


            }
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in SMS_Report ** (handleRequest)", context, e, "SMS_Report", "handleRequest", conn);
            Services.DumException("SMS_Report", "Handle Request", request, e, getServletContext());
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
                helper.SendEmailWithAttachment("Error in SMS_Report ** (handleRequest -- SqlException)", context, e, "SMS_Report", "handleRequest", conn);
                Services.DumException("SMS_Report", "Handle Request", request, e, getServletContext());
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("FormName", "ManagementDashboard");
                Parser.SetField("ActionID", "GetInput");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/ExceptionMessage.html");
            }
            out.flush();
            out.close();
        }
    }


    private void GetInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, String Database, int ClientId, UtilityHelper helper, TwilioSMSConfiguration smsConfiguration, int userIndex) throws FileNotFoundException {
        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";

        Statement stmt0 = null;
        ResultSet rset0 = null;
        String Query0 = "";
        int PatientCount = 0;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();

        StringBuffer CDRList = new StringBuffer();


        StringBuffer FacilityList = new StringBuffer();


        try {
            Query = "Select Id, name from oe.clients where (status = 0 or status=1) AND (Id NOT IN (23,30,31,32,36,33)) ORDER BY name ASC";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            FacilityList.append("<option value='' disabled selected>Select Facility</option>");
            while (rset.next()) {
                FacilityList.append("<option value='" + rset.getString(1) + "' >" + rset.getString(2) + "</option>");
            }
            stmt.close();
            rset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        try {
            try {
                Query0 = "Select Id, IFNULL(dbname,''), status, name from oe.clients where status = 0 ";
                stmt0 = conn.createStatement();
                rset0 = stmt0.executeQuery(Query0);

                while (rset0.next()) {
                    Query = "SELECT PatientMRN,PatientName,PatientPhNumber,Sms,SentAt," +
                            " CASE" +
                            " WHEN Priority=1 THEN \"Low\"   \n" +
                            " WHEN Priority=2 THEN \"Medium\"   \n" +
                            " WHEN Priority=3 THEN \"High\"" +
                            " END, " +
                            "CASE" +
                            " WHEN isSchedulerSent=1 THEN \"Sent\"   \n" +
                            " WHEN isSchedulerSent=0 THEN \"In-Process\"   \n" +
                            " WHEN isSchedulerSent=999 THEN \"Error\"" +
                            "END" +
                            " FROM " + rset0.getString(2) + ".SMS_Info " +
                            "where Status=0 " +
                            " AND " +
                            " SentAt between '" + df.format(currentDate) + " 00:00:00' and '" + df.format(currentDate) + " 23:59:59' " +
                            " ORDER BY SentAt Desc";
                    //out.println(Query);
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        CDRList.append("<tr>\n");
                        CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");//MRN
                        CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");//Name
                        CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");//PhNumber
                        CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");//Sms
                        CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");//SentAt
                        CDRList.append("<td align=left>" + rset0.getString(4) + "</td>\n");//Facility
                        CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");//Priority
                        CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");//Status
                        CDRList.append("</tr>");
                    }
                    rset.close();
                    stmt.close();
                }
                rset0.close();
                stmt0.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Query0 = "Select Id, IFNULL(dbname,''), status, name from oe.clients where status = 1 ";
            stmt0 = conn.createStatement();
            rset0 = stmt0.executeQuery(Query0);
            while (rset0.next()) {
                Query = "SELECT PatientMRN,PatientName,PatientPhNumber,Sms,SentAt," +
                        " CASE" +
                        " WHEN Priority=1 THEN \"Low\"   \n" +
                        " WHEN Priority=2 THEN \"Medium\"   \n" +
                        " WHEN Priority=3 THEN \"High\"" +
                        " END, " +
                        "CASE" +
                        " WHEN isSchedulerSent=1 THEN \"Sent\"   \n" +
                        " WHEN isSchedulerSent=0 THEN \"In-Process\"   \n" +
                        " WHEN isSchedulerSent=999 THEN \"Error\"" +
                        "END" +
                        " FROM oe.SMS_Info " +
                        " where Status=0 AND FacilityIdx=" + rset0.getString(1) + " \n" +
                        " AND " +
                        " SentAt between '" + df.format(currentDate) + " 00:00:00' and '" + df.format(currentDate) + " 23:59:59' " +
                        " ORDER BY SentAt Desc";
                //out.println(Query);
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    CDRList.append("<tr>\n");
                    CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");//MRN
                    CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");//Name
                    CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");//PhNumber
                    CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");//Sms
                    CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");//SentAt
                    CDRList.append("<td align=left>" + rset0.getString(4) + "</td>\n");//Facility
                    CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");//Priority
                    CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");//Status
                    CDRList.append("</tr>");
                }
                rset.close();
                stmt.close();
            }
            rset0.close();
            stmt0.close();


            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("from", String.valueOf(df.format(currentDate)));
            Parser.SetField("To", String.valueOf(df.format(currentDate)));
            Parser.SetField("FacilityList", String.valueOf(FacilityList));
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("Today", df.format(currentDate));

            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/SMS_Report.html");
        } catch (Exception e) {
            helper.SendEmailWithAttachment("Error in GetInput ** (SMS_Report^^ MES#001)", servletContext, e, "SMS_Report", "GetInput", conn);
            Services.DumException("SMS_Report", "GetInput", request, e);
            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("FormName", "ManagementDashboard");
            Parser.SetField("ActionID", "GetInput");
            Parser.SetField("Message", "MES#001");
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Exception/ExceptionMessage.html");
        }
    }

    void GetReport(HttpServletRequest request, PrintWriter out, Connection conn, ServletContext servletContext, int UserId, String Database, int ClientId, TwilioSMSConfiguration smsConfiguration) {
        String FromDate = request.getParameter("FromDate").trim();
        String ToDate = request.getParameter("ToDate").trim();

        String Facility = (request.getParameter("Facility") == null) ? "" : request.getParameter("Facility").trim();


        StringBuffer CDRList = new StringBuffer();

        Statement stmt = null;
        ResultSet rset = null;
        String Query = "";

        Statement stmt0 = null;
        ResultSet rset0 = null;
        String Query0 = "";
        String FacilityCheck = "";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();

        StringBuffer FacilityList = new StringBuffer();

        if (!Facility.equals("")) {
            FacilityCheck = "AND Id=" + Facility + ";";
        } else {
            FacilityCheck = ";";
        }

        try {
            Query = "Select Id, name from oe.clients where (status = 0 or status=1) AND (Id NOT IN (23,30,31,32,36,33)) ORDER BY name ASC";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            FacilityList.append("<option value=''>Select ALL</option>");
            while (rset.next()) {
                if (Facility.equals(rset.getString(1))) {
                    FacilityList.append("<option value='" + rset.getString(1) + "' selected>" + rset.getString(2) + "</option>");
                } else {
                    FacilityList.append("<option value='" + rset.getString(1) + "' >" + rset.getString(2) + "</option>");
                }

            }
            stmt.close();
            rset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {

            try {
                Query0 = "Select Id, IFNULL(dbname,''), status, name from oe.clients where status = 0 " + FacilityCheck;
                stmt0 = conn.createStatement();
                rset0 = stmt0.executeQuery(Query0);

                while (rset0.next()) {
                    Query = "SELECT PatientMRN,PatientName,PatientPhNumber,Sms,SentAt," +
                            " CASE" +
                            " WHEN Priority=1 THEN \"Low\"   \n" +
                            " WHEN Priority=2 THEN \"Medium\"   \n" +
                            " WHEN Priority=3 THEN \"High\"" +
                            " END, " +
                            "CASE" +
                            " WHEN isSchedulerSent=1 THEN \"Sent\"   \n" +
                            " WHEN isSchedulerSent=0 THEN \"In-Process\"   \n" +
                            " WHEN isSchedulerSent=999 THEN \"Error\"" +
                            "END" +
                            " FROM " + rset0.getString(2) + ".SMS_Info " +
                            "where Status=0 " +
                            " AND " +
                            " SentAt between '" + FromDate + " 00:00:00' and '" + ToDate + " 23:59:59' " +
                            " ORDER BY SentAt Desc";
                    //out.println(Query);
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(Query);
                    while (rset.next()) {
                        CDRList.append("<tr>\n");
                        CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");//MRN
                        CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");//Name
                        CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");//PhNumber
                        CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");//Sms
                        CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");//SentAt
                        CDRList.append("<td align=left>" + rset0.getString(4) + "</td>\n");//Facility
                        CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");//Priority
                        CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");//Status
                        CDRList.append("</tr>");
                    }
                    rset.close();
                    stmt.close();
                }
                rset0.close();
                stmt0.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Query0 = "Select Id, IFNULL(dbname,''), status, name from oe.clients where status = 1 " + FacilityCheck;
            stmt0 = conn.createStatement();
            rset0 = stmt0.executeQuery(Query0);
            while (rset0.next()) {
                Query = "SELECT PatientMRN,PatientName,PatientPhNumber,Sms,SentAt," +
                        " CASE" +
                        " WHEN Priority=1 THEN \"Low\"   \n" +
                        " WHEN Priority=2 THEN \"Medium\"   \n" +
                        " WHEN Priority=3 THEN \"High\"" +
                        " END, " +
                        "CASE" +
                        " WHEN isSchedulerSent=1 THEN \"Sent\"   \n" +
                        " WHEN isSchedulerSent=0 THEN \"In-Process\"   \n" +
                        " WHEN isSchedulerSent=999 THEN \"Error\"" +
                        "END" +
                        " FROM oe.SMS_Info " +
                        "where Status=0 AND FacilityIdx=" + rset0.getString(1) + " \n" +
                        " AND " +
                        " SentAt between '" + FromDate + " 00:00:00' and '" + ToDate + " 23:59:59' " +
                        " ORDER BY SentAt Desc";
                //out.println(Query);
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                while (rset.next()) {
                    CDRList.append("<tr>\n");
                    CDRList.append("<td align=left>" + rset.getString(1) + "</td>\n");//MRN
                    CDRList.append("<td align=left>" + rset.getString(2) + "</td>\n");//Name
                    CDRList.append("<td align=left>" + rset.getString(3) + "</td>\n");//PhNumber
                    CDRList.append("<td align=left>" + rset.getString(4) + "</td>\n");//Sms
                    CDRList.append("<td align=left>" + rset.getString(5) + "</td>\n");//SentAt
                    CDRList.append("<td align=left>" + rset0.getString(4) + "</td>\n");//Facility
                    CDRList.append("<td align=left>" + rset.getString(6) + "</td>\n");//Priority
                    CDRList.append("<td align=left>" + rset.getString(7) + "</td>\n");//Status
                    CDRList.append("</tr>");
                }
                rset.close();
                stmt.close();
            }
            rset0.close();
            stmt0.close();


            final Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("from", String.valueOf(FromDate));
            Parser.SetField("To", String.valueOf(ToDate));
            Parser.SetField("FacilityList", String.valueOf(FacilityList));
//            Parser.SetField("Facility", String.valueOf(Facility));
            Parser.SetField("CDRList", String.valueOf(CDRList));
            Parser.SetField("Today", df.format(currentDate));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Reports/SMS_Report.html");
        } catch (Exception e) {
            Services.DumException("SMS_Report", "SMS Report Error: ", request, e, this.getServletContext());
            return;
        }
    }

}