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
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//import com.itextpdf.text.pdf.PdfWriter;

@SuppressWarnings("Duplicates")
public class LabDashboard extends HttpServlet {
    Integer ScreenIndex = 1;
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

    public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String UserId;
        int FacilityIndex;
        String DatabaseName;
        String FontColor;
        PrintWriter out = new PrintWriter((OutputStream) response.getOutputStream());
        Services supp = new Services();
        int UserIndex = 0;
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
            FontColor = session.getAttribute("FontColor").toString();
            FacilityIndex = Integer.parseInt(session.getAttribute("FacilityIndex").toString());
            UserIndex = Integer.parseInt(session.getAttribute("UserIndex").toString());


            try {
/*                boolean ValidSession = FacilityLogin.checkSession(out, request, context, response);
                if (!ValidSession) {
                    out.flush();
                    out.close();
                    return;
                }*/
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


            ActionID = request.getParameter("ActionID").trim();
            conn = Services.getMysqlConn(context);


//            if(!helper.AuthorizeScreen(request,out,conn,context,UserIndex,this.ScreenIndex)){
////                out.println("You are not Authorized to access this page");
//                Parsehtm Parser = new Parsehtm(request);
//                Parser.SetField("Message", "You are not Authorized to access this page");
//                Parser.SetField("FormName", "ManagementDashboard");
//                Parser.SetField("ActionID", "GetInput");
//                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "Exception/Message.html");
//                return;
//            }


            if (conn == null) {
                Parsehtm Parser = new Parsehtm(request);
                Parser.SetField("Error", "Unable to connect. Our team is looking into it!");
                Parser.GenerateHtml(out, Services.GetHtmlPath(context) + "FacilityLogin.html");
                return;
            }
            if (ActionID.equals("GetInput")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, "LAB Management Dashboard", "View LAB Management Dashboard", FacilityIndex);
                GetInput(request, out, conn, context, UserId, DatabaseName, FacilityIndex, FontColor);
            } else if (ActionID.equals("BillingDashboard")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, " LAB Billing Dashboard", "View Billing Dashboard", FacilityIndex);
                BillingDashboard(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
            } else if (ActionID.equals("BillingDashboard_DATEWISE")) {
                supp.Dologing(UserId, conn, request.getRemoteAddr(), ActionID, " LAB Billing Dashboard", "View Billing Dashboard", FacilityIndex);
                BillingDashboard_DATEWISE(request, out, conn, context, UserId, DatabaseName, FacilityIndex);
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


    void GetInput(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, String UserId, String Database, int ClientId, String FontColor) {

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

        String OrdersCurrentMonthDaily = "";
        String PatientCountAgeWise = "";
        String CurrentYear = "";
        String DateNow = "";
        String OrderCountMonthly = "";
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
        int OrdersRegistered_TODAY = 0;
        int OrdersDispatched_TODAY = 0;
        int OrdersReceivedAtLab_TODAY = 0;
        double OrdersResultAnnounced_TODAY = 0;
        double OrdersBacklog_TODAY = 0;
        double Orders_TOTAL_TODAY = 0;
        double SuccessRatio_TODAY = 0;
        double BackLogRatio_TODAY = 0;
        int PatientCountInsured_TODAY = 0;
        int PatientCountSelfPay_TODAY = 0;
        int PatientCountWC_TODAY = 0;
        int PatientCountMVA_TODAY = 0;
        int PatientCountCOVID_TODAY = 0;


        String WEEK_START = "";
        String WEEK_END = "";
        int OrdersRegistered_WEEKLY = 0;
        int OrdersDispatched_WEEKLY = 0;
        int OrdersReceivedAtLab_WEEKLY = 0;
        double OrdersResultAnnounced_WEEKLY = 0;
        double OrdersBacklog_WEEKLY = 0;
        double Orders_TOTAL_WEEKLY = 0;
        double SuccessRatio_WEEKLY = 0;
        double BackLogRatio_WEEKLY = 0;

        String MONTH_START = "";
        String MONTH_END = "";
        int OrdersRegistered_MONTHLY = 0;
        int OrdersDispatched_MONTHLY = 0;
        int OrdersReceivedAtLab_MONTHLY = 0;
        double OrdersResultAnnounced_MONTHLY = 0;
        double OrdersBacklog_MONTHLY = 0;
        double Orders_TOTAL_MONTHLY = 0;
        double SuccessRatio_MONTHLY = 0;
        double BackLogRatio_MONTHLY = 0;

        String LAST_MONTH_START = "";
        String LAST_MONTH_END = "";
        int PatientCountOverAll_LAST_MONTHLY = 0;

        String OrdersCurrentWeekDaily = "";

        int presentDayNumber = 0;


        try {

            /*----------------------------------------------------------------------------------------------
             *                             Getting Time Variables
             * ---------------------------------------------------------------------------------------------
             */
            Query = "Select DATE_FORMAT(NOW(),'%Y-%m-%d'), " +
                    "DATE_ADD(DATE_FORMAT(NOW(),'%Y-%m-%d'), INTERVAL(1-DAYOFWEEK(DATE_FORMAT(NOW(),'%Y-%m-%d'))) DAY), " +
                    "DATE_ADD(DATE_ADD(DATE_FORMAT(NOW(),'%Y-%m-%d'), INTERVAL(1-DAYOFWEEK(DATE_FORMAT(NOW(),'%Y-%m-%d'))) DAY),INTERVAL 6 DAY), " +
                    "DATE_SUB(LAST_DAY(NOW()),INTERVAL DAY(LAST_DAY(NOW()))- 1 DAY) AS 'FIRST DAY OF CURRENT MONTH', " +
                    "LAST_DAY(NOW())," +
                    "DATE_SUB(LAST_DAY(DATE_FORMAT(NOW(),'%Y-%m-%d') - INTERVAL 1 MONTH),INTERVAL DAY(LAST_DAY(DATE_FORMAT(NOW(),'%Y-%m-%d') - INTERVAL 1 MONTH))- 1 DAY) AS 'FIRST DAY OF LAST MONTH'," +
                    "LAST_DAY(DATE_FORMAT(NOW(),'%Y-%m-%d') - INTERVAL 1 MONTH )," +
                    " Date_format(NOW(),'%d')";


            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                TODAY = rset.getString(1);
                WEEK_START = rset.getString(2);
                WEEK_END = rset.getString(3);
                MONTH_START = rset.getString(4);
                MONTH_END = rset.getString(5);
                LAST_MONTH_START = rset.getString(6);
                LAST_MONTH_END = rset.getString(7);
                presentDayNumber = rset.getInt(8);
            }
            rset.close();
            stmt.close();



            /*----------------------------------------------------------------------------------------------
             *                              TODAY
             * ---------------------------------------------------------------------------------------------
             */

            Query = "Select COUNT(*) from " + Database + ".TestOrder a " +
                    "where DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + TODAY + " 00:00:00' " +
                    "AND DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + TODAY + " 23:59:59' " +
                    "AND StageIdx=0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                OrdersRegistered_TODAY = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            Query = "Select COUNT(*) from " + Database + ".TestOrder a " +
                    "where DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + TODAY + " 00:00:00' " +
                    "AND DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + TODAY + " 23:59:59' " +
                    "AND StageIdx=1";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                OrdersDispatched_TODAY = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from " + Database + ".TestOrder a " +
                    "where DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + TODAY + " 00:00:00' " +
                    "AND DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + TODAY + " 23:59:59' " +
                    "AND StageIdx=2";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                OrdersReceivedAtLab_TODAY = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            Query = "Select COUNT(*) from " + Database + ".Tests a" +
                    " INNER JOIN " + Database + ".TestOrder b ON b.Id = a.OrderId" +
                    " where DATE_FORMAT(b.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + TODAY + " 00:00:00' " +
                    "AND DATE_FORMAT(b.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + TODAY + " 23:59:59' " +
                    "AND a.TestStatus is not null";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                OrdersResultAnnounced_TODAY = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            Query = "Select COUNT(*) from " + Database + ".Tests a" +
                    " INNER JOIN " + Database + ".TestOrder b ON b.Id = a.OrderId" +
                    " where DATE_FORMAT(b.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + TODAY + " 00:00:00' " +
                    "AND DATE_FORMAT(b.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + TODAY + " 23:59:59' " +
                    "AND a.TestStatus is null";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                OrdersBacklog_TODAY = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            Query = "Select COUNT(*) from " + Database + ".Tests a" +
                    " INNER JOIN " + Database + ".TestOrder b ON b.Id = a.OrderId" +
                    " where DATE_FORMAT(b.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + TODAY + " 00:00:00' " +
                    "AND DATE_FORMAT(b.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + TODAY + " 23:59:59' ";

            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Orders_TOTAL_TODAY = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            if (Orders_TOTAL_TODAY != 0.f) {
                SuccessRatio_TODAY = (OrdersResultAnnounced_TODAY / Orders_TOTAL_TODAY) * 100;
                BackLogRatio_TODAY = (OrdersBacklog_TODAY / Orders_TOTAL_TODAY) * 100;
            } else {
                SuccessRatio_TODAY = 0.f;
                BackLogRatio_TODAY = 0.f;
            }


            System.out.println("OrdersResultAnnounced_TODAY " + OrdersResultAnnounced_TODAY);
            System.out.println("Orders_TOTAL_TODAY -> " + Orders_TOTAL_TODAY);
            System.out.println("SuccessRatio_TODAY -> " + SuccessRatio_TODAY);







            /*----------------------------------------------------------------------------------------------
             *                              WEEKLY
             * ---------------------------------------------------------------------------------------------
             */

            Query = "Select COUNT(*) from " + Database + ".TestOrder a " +
                    "where DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + WEEK_START + " 00:00:00' " +
                    "AND DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + WEEK_END + " 23:59:59' " +
                    "AND StageIdx=0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                OrdersRegistered_WEEKLY = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            Query = "Select COUNT(*) from " + Database + ".TestOrder a " +
                    "where DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + WEEK_START + " 00:00:00' " +
                    "AND DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + WEEK_END + " 23:59:59' " +
                    "AND StageIdx=1";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                OrdersDispatched_WEEKLY = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from " + Database + ".TestOrder a " +
                    "where DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + WEEK_START + " 00:00:00' " +
                    "AND DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + WEEK_END + " 23:59:59' " +
                    "AND StageIdx=2";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                OrdersReceivedAtLab_WEEKLY = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            Query = "Select COUNT(*) from " + Database + ".Tests a" +
                    " INNER JOIN " + Database + ".TestOrder b ON b.Id = a.OrderId" +
                    " where DATE_FORMAT(b.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + WEEK_START + " 00:00:00' " +
                    "AND DATE_FORMAT(b.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + WEEK_END + " 23:59:59' " +
                    "AND a.TestStatus is not null";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                OrdersResultAnnounced_WEEKLY = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            Query = "Select COUNT(*) from " + Database + ".Tests a" +
                    " INNER JOIN " + Database + ".TestOrder b ON b.Id = a.OrderId" +
                    " where DATE_FORMAT(b.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + WEEK_START + " 00:00:00' " +
                    "AND DATE_FORMAT(b.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + WEEK_END + " 23:59:59' " +
                    "AND a.TestStatus is null";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                OrdersBacklog_WEEKLY = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            Query = "Select COUNT(*) from " + Database + ".Tests a" +
                    " INNER JOIN " + Database + ".TestOrder b ON b.Id = a.OrderId" +
                    " where DATE_FORMAT(b.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + WEEK_START + " 00:00:00' " +
                    "AND DATE_FORMAT(b.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + WEEK_END + " 23:59:59' ";

            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Orders_TOTAL_WEEKLY = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            if (Orders_TOTAL_WEEKLY != 0.f) {
                SuccessRatio_WEEKLY = (OrdersResultAnnounced_WEEKLY / Orders_TOTAL_WEEKLY) * 100;
                BackLogRatio_WEEKLY = (OrdersBacklog_WEEKLY / Orders_TOTAL_WEEKLY) * 100;
            } else {
                SuccessRatio_WEEKLY = 0.f;
                BackLogRatio_WEEKLY = 0.f;
            }


            System.out.println("OrdersResultAnnounced_WEEKLY -> " + OrdersResultAnnounced_WEEKLY);
            System.out.println("Orders_TOTAL_WEEKLY -> " + Orders_TOTAL_WEEKLY);
            System.out.println("SuccessRatio_WEEKLY -> " + SuccessRatio_WEEKLY);




            /*----------------------------------------------------------------------------------------------
             *                              MONTHLY
             * ---------------------------------------------------------------------------------------------
             */

            Query = "Select COUNT(*) from " + Database + ".TestOrder a " +
                    "where DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + MONTH_START + " 00:00:00' " +
                    "AND DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + MONTH_END + " 23:59:59' " +
                    "AND StageIdx=0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                OrdersRegistered_MONTHLY = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            Query = "Select COUNT(*) from " + Database + ".TestOrder a " +
                    "where DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + MONTH_START + " 00:00:00' " +
                    "AND DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + MONTH_END + " 23:59:59' " +
                    "AND StageIdx=1";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                OrdersDispatched_MONTHLY = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from " + Database + ".TestOrder a " +
                    "where DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + MONTH_START + " 00:00:00' " +
                    "AND DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + MONTH_END + " 23:59:59' " +
                    "AND StageIdx=2";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                OrdersReceivedAtLab_MONTHLY = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            Query = "Select COUNT(*) from " + Database + ".Tests a" +
                    " INNER JOIN " + Database + ".TestOrder b ON b.Id = a.OrderId" +
                    " where DATE_FORMAT(b.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + MONTH_START + " 00:00:00' " +
                    "AND DATE_FORMAT(b.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + MONTH_END + " 23:59:59' " +
                    "AND a.TestStatus is not null";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                OrdersResultAnnounced_MONTHLY = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            Query = "Select COUNT(*) from " + Database + ".Tests a" +
                    " INNER JOIN " + Database + ".TestOrder b ON b.Id = a.OrderId" +
                    " where DATE_FORMAT(b.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + MONTH_START + " 00:00:00' " +
                    "AND DATE_FORMAT(b.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + MONTH_END + " 23:59:59' " +
                    "AND a.TestStatus is null";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                OrdersBacklog_MONTHLY = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            Query = "Select COUNT(*) from " + Database + ".Tests a" +
                    " INNER JOIN " + Database + ".TestOrder b ON b.Id = a.OrderId" +
                    " where DATE_FORMAT(b.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + MONTH_START + " 00:00:00' " +
                    "AND DATE_FORMAT(b.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + MONTH_END + " 23:59:59' ";

            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Orders_TOTAL_MONTHLY = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            if (Orders_TOTAL_MONTHLY != 0.f) {
                SuccessRatio_MONTHLY = (OrdersResultAnnounced_MONTHLY / Orders_TOTAL_MONTHLY) * 100;
                BackLogRatio_MONTHLY = (OrdersBacklog_MONTHLY / Orders_TOTAL_MONTHLY) * 100;
            } else {
                SuccessRatio_MONTHLY = 0.f;
                BackLogRatio_MONTHLY = 0.f;
            }


            System.out.println("OrdersResultAnnounced_MONTHLY -> " + OrdersResultAnnounced_MONTHLY);
            System.out.println("Orders_TOTAL_MONTHLY -> " + Orders_TOTAL_MONTHLY);
            System.out.println("SuccessRatio_MONTHLY -> " + SuccessRatio_MONTHLY);
            System.out.println("OrdersResultAnnounced_MONTHLY / Orders_TOTAL_MONTHLY -> " + OrdersResultAnnounced_MONTHLY / Orders_TOTAL_MONTHLY);




            /*----------------------------------------------------------------------------------------------
             *                             WEEKLY TREND
             * ---------------------------------------------------------------------------------------------
             */
            Query = "SELECT * FROM  (SELECT adddate('1970-01-01',t4.i*10000 + t3.i*1000 + t2.i*100 + t1.i*10 + t0.i) selected_date FROM \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t0, \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t1, \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t2, \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t3, \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t4) v \n" +
                    "WHERE selected_date >= '" + WEEK_START + "' AND selected_date <= '" + WEEK_END + "' ORDER BY selected_date";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                _FromEndDate = rset.getString(1);
                _ToEndDate = rset.getString(1);
//                Query1 = "Select COUNT(*) from " + Database + ".PatientVisit  WHERE DateOfService >= '" + _FromEndDate + " 00:00:00' and DateOfService <= '" + _ToEndDate + " 23:59:59'";
                Query1 = "Select COUNT(*) from " + Database + ".TestOrder a " +
                        " where DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + _FromEndDate + " 00:00:00'" +
                        " and DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + _ToEndDate + " 23:59:59' " +
                        " AND a.StageIdx=0";
                stmt1 = conn.createStatement();
                rset1 = stmt1.executeQuery(Query1);
                while (rset1.next()) {
                    OrdersCurrentWeekDaily += rset1.getInt(1) + " , ";
                }
                rset1.close();
                stmt1.close();

            }
            rset.close();
            stmt.close();

            if (OrdersCurrentWeekDaily.endsWith(",")) {
                OrdersCurrentWeekDaily = OrdersCurrentWeekDaily.substring(0, OrdersCurrentWeekDaily.length() - 1);
            }


//            Query = "Select Id,Concat(DoctorsLastName, ',', DoctorsFirstName) from " + Database + ".DoctorsList where Status = 1";
//            //out.println(Query);
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            while (rset.next()) {
//                DoctorsList.append("<div class=\"d-flex align-items-center justify-content-between my-15 pr-20\">");
//                DoctorsList.append("<h5 class=\"my-0\"><i class=\"mr-5 w-20 fa fa-user-md\"></i>" + rset.getString(2) + "</h5>");
//
//                Query1 = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and DoctorsName = " + rset.getInt(1);
//                stmt1 = conn.createStatement();
//                rset1 = stmt1.executeQuery(Query1);
//                if (rset1.next()) {
//                    DoctorsList.append("<p class=\"mb-0\">" + rset1.getInt(1) + "</p>");
//                }
//                rset1.close();
//                stmt1.close();
//
//                DoctorsList.append("</div>");
//            }
//            rset.close();
//            stmt.close();

//            Query = "SELECT DATE_SUB(LAST_DAY(NOW()),INTERVAL DAY(LAST_DAY(NOW()))- 1 DAY) AS 'FIRST DAY OF CURRENT MONTH', LAST_DAY( now() ), YEAR(NOW())";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                FromDate = rset.getString(1);
//                ToDate = rset.getString(2);
//            }
//            rset.close();
//            stmt.close();

//            Query = "SELECT DATE_FORMAT(NOW(),'%Y-%m-%d'), DATE_FORMAT(NOW(),'%m/%d/%Y')";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                CurrentDate = rset.getString(1);
//                DateNow = rset.getString(2);
//            }
//            rset.close();
//            stmt.close();

            Query = "SELECT * FROM  (SELECT adddate('1970-01-01',t4.i*10000 + t3.i*1000 + t2.i*100 + t1.i*10 + t0.i) selected_date FROM \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t0, \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t1, \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t2, \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t3, \n" +
                    "(SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t4) v \n" +
                    "WHERE selected_date >= '" + MONTH_START + "' AND selected_date <= '" + MONTH_END + "' ORDER BY selected_date";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                _FromEndDate = rset.getString(1);
                _ToEndDate = rset.getString(1);
//                Query1 = "Select COUNT(*) from " + Database + ".PatientVisit  WHERE DateOfService >= '" + _FromEndDate + " 00:00:00' and DateOfService <= '" + _ToEndDate + " 23:59:59'";
                Query1 = "Select COUNT(*) from " + Database + ".TestOrder a " +
                        " where DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') >= '" + _FromEndDate + " 00:00:00'" +
                        " and DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s') <= '" + _ToEndDate + " 23:59:59' " +
                        " AND a.StageIdx=0";
                stmt1 = conn.createStatement();
                rset1 = stmt1.executeQuery(Query1);
                while (rset1.next()) {
                    OrdersCurrentMonthDaily += rset1.getInt(1) + " , ";
                }
                rset1.close();
                stmt1.close();

            }
            rset.close();
            stmt.close();

            if (OrdersCurrentMonthDaily.endsWith(",")) {
                OrdersCurrentMonthDaily = OrdersCurrentMonthDaily.substring(0, OrdersCurrentMonthDaily.length() - 1);
            }

//            Query = "Select COUNT(*) from " + Database + ".PatientVisit where   DateOfService >= '" + FromDate + " 00:00:00' and DateOfService <= '" + ToDate + " 23:59:59'";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountCurrentMonth = rset.getInt(1);
//            }
//            rset.close();
//            stmt.close();

//            Query = "Select COUNT(*) from " + Database + ".PatientVisit ";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountOverAll = rset.getInt(1);
//            }
//            rset.close();
//            stmt.close();

//            Query = "Select COUNT(*) from " + Database + ".PatientVisit ";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountMale = rset.getInt(1);
//            }
//            rset.close();
//            stmt.close();
//
//            Query = "Select COUNT(*) from " + Database + ".PatientVisit ";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountFemale = rset.getInt(1);
//            }
//            rset.close();
//            stmt.close();
//
//            if (ClientId == 9 || ClientId == 28) {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details where HealthInsuranceChk = 0";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    PatientCountSelfPay = rset.getInt(1);
//                }
//                rset.close();
//                stmt.close();
//            } else {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and SelfPayChk = 0";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    PatientCountSelfPay = rset.getInt(1);
//                }
//                rset.close();
//                stmt.close();
//            }
//
//
//            if (ClientId == 9 || ClientId == 28) {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details where HealthInsuranceChk = 1";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    PatientCountInsured = rset.getInt(1);
//                }
//                rset.close();
//                stmt.close();
//            } else {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and SelfPayChk = 1";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    PatientCountInsured = rset.getInt(1);
//                }
//                rset.close();
//                stmt.close();
//            }
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and Age >= 0 and Age <= 5";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountAgeWise += rset.getInt(1) + " , ";
//            }
//            rset.close();
//            stmt.close();
//
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and Age >= 6 and Age <= 10";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountAgeWise += rset.getInt(1) + " , ";
//            }
//            rset.close();
//            stmt.close();
//
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and Age >= 11 and Age <= 15";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountAgeWise += rset.getInt(1) + " , ";
//            }
//            rset.close();
//            stmt.close();
//
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and Age >= 16 and Age <= 20";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountAgeWise += rset.getInt(1) + " , ";
//            }
//            rset.close();
//            stmt.close();
//
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and Age >= 21 and Age <= 25";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountAgeWise += rset.getInt(1) + " , ";
//            }
//            rset.close();
//            stmt.close();
//
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and Age >= 26 and Age <= 30";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountAgeWise += rset.getInt(1) + " , ";
//            }
//            rset.close();
//            stmt.close();
//
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and Age >= 31 and Age <= 35";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountAgeWise += rset.getInt(1) + " , ";
//            }
//            rset.close();
//            stmt.close();
//
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and Age >= 36 and Age <= 40";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountAgeWise += rset.getInt(1) + " , ";
//            }
//            rset.close();
//            stmt.close();
//
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and Age > 40 ";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountAgeWise += rset.getInt(1) + " , ";
//            }
//            rset.close();
//            stmt.close();
//
//
//            if (PatientCountAgeWise.endsWith(",")) {
//                PatientCountAgeWise = PatientCountAgeWise.substring(0, PatientCountAgeWise.length() - 1);
//            }

            Query = "SELECT YEAR(NOW())";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                CurrentYear = rset.getString(1);
            }
            rset.close();
            stmt.close();

            String[] Months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

            for (int i = 0; i <= 11; i++) {
//                Query = "Select COUNT(*) from " + Database + ".PatientVisit where  DATE_FORMAT(DateOfService,'%Y-%m') = '" + CurrentYear + "-" + Months[i] + "'";
                Query = "Select COUNT(*) from " + Database + ".TestOrder a " +
                        " where DATE_FORMAT(DATE_FORMAT(a.OrderDate,'%Y-%m-%d %h:%i:%s'),'%Y-%m') = '" + CurrentYear + "-" + Months[i] + "'" +
                        " AND a.StageIdx=0";
                stmt = conn.createStatement();
                rset = stmt.executeQuery(Query);
                if (rset.next()) {
                    OrderCountMonthly += rset.getString(1) + ",";
                }
                rset.close();
                stmt.close();
            }
            if (OrderCountMonthly.endsWith(",")) {
                OrderCountMonthly = OrderCountMonthly.substring(0, OrderCountMonthly.length() - 1);
            }


//            if (ClientId == 9 || ClientId == 28) {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details where WorkersCompPolicyChk = 1 ";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    PatientCountWC = rset.getInt(1);
//                }
//                rset.close();
//                stmt.close();
//            } else {
//                Query = "Select COUNT(*) from " + Database + ".InsuranceInfo where WorkersCompPolicy = 1 ";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    PatientCountWC = rset.getInt(1);
//                }
//                rset.close();
//                stmt.close();
//            }
//
//            if (ClientId == 9 || ClientId == 28) {
//                Query = "Select COUNT(*) from " + Database + ".PatientReg_Details where MotorVehicleAccidentChk = 1 ";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    PatientCountMVA = rset.getInt(1);
//                }
//                rset.close();
//                stmt.close();
//            } else {
//                Query = "Select COUNT(*) from " + Database + ".InsuranceInfo where MotorVehAccident = 1 ";
//                stmt = conn.createStatement();
//                rset = stmt.executeQuery(Query);
//                if (rset.next()) {
//                    PatientCountMVA = rset.getInt(1);
//                }
//                rset.close();
//                stmt.close();
//            }

//            //Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and ltrim(rtrim(UPPER(ReasonVisit))) =  'COVID TESTING'";
//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and COVIDStatus = 1";
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountCOVID = rset.getInt(1);
//            }
//            rset.close();
//            stmt.close();

//            Query = "Select COUNT(*) from " + Database + ".PatientReg where Status = 0 and DATE_FORMAT(DateOfService,'%Y-%m-%d') = '" + CurrentDate + "' ";
////      out.println(Query);
//            stmt = conn.createStatement();
//            rset = stmt.executeQuery(Query);
//            if (rset.next()) {
//                PatientCountToday = rset.getInt(1);
//            }
//            rset.close();
//            stmt.close();

//      System.out.println(PatientCountMonthly);

            final Parsehtm Parser = new Parsehtm(request);
//
//            presentDayNumber -=1;
//
//            if(presentDayNumber==0)
//            {
//                presentDayNumber=1;
//            }
            double Patient_Differential = 0.0;
            double Patient_difference = 0;
            double AVG_difference = 0;
            String Differential_badge = "";
//            double PatientCountOverAll_MONTHLY_AVG = (double)PatientCountOverAll_MONTHLY/(double)presentDayNumber;
//            double PatientCountOverAll_LAST_MONTHLY_AVG = (double)PatientCountOverAll_LAST_MONTHLY/(double)30;

//            if(PatientCountOverAll_LAST_MONTHLY_AVG==0){
//                CDRList.append("<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill  blink_me\" style=\"font-size: 100%;border-style: double;\"> undefined </span> </td>\n");
//            }else{
//                AVG_difference = ((PatientCountOverAll_MONTHLY_AVG/PatientCountOverAll_LAST_MONTHLY_AVG)*100)-100;
//
//                if(AVG_difference==0){
//                    Differential_badge = "<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill  blink_me\" style=\"float: right;font-size: 100%;border-style: double;\">  "+String.format("%,.0f", AVG_difference)+"%</span> </td>\n";
//                }else if (AVG_difference>0){
//                    Differential_badge = "<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill bg-success blink_me\" style=\"float: right;font-size: 100%;border-style: double;border-color: aquamarine;\"> +" + String.format("%,.0f", AVG_difference) + "% </span></td>\n";
//                }
//                else if (AVG_difference<0){
//                    Differential_badge = "<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill bg-danger blink_me\" style=\"float: right;font-size: 100%;border-style: double;border-color: #fa0b20;\"> "+String.format("%,.0f", AVG_difference)+"%</span> </td>\n";
//                }
//            }

//            if(PatientCountOverAll_MONTHLY_AVG > PatientCountOverAll_LAST_MONTHLY_AVG){
//                //INCREASE
//                Patient_difference = PatientCountOverAll_MONTHLY_AVG - PatientCountOverAll_LAST_MONTHLY_AVG;
//                if (PatientCountOverAll_LAST_MONTHLY_AVG==0){
//                    Differential_badge = "<span class=\"badge badge-pill bg-success blink_me\" style=\"float: right; font-size: 100%;border-style: double;border-color: aquamarine;\"> +100% Increase</span>";
//                }else{
//                Patient_Differential = (((double)Patient_difference/(double)PatientCountOverAll_LAST_MONTHLY_AVG))*100;
//                if(Patient_Differential==0){
//                    CDRList.append("<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill  blink_me\" style=\"font-size: 100%;border-style: double;\">  "+String.format("%,.0f", Patient_Differential)+"%</span> </td>\n");
//                }else{
//                    Differential_badge = "<span class=\"badge badge-pill bg-success blink_me\" style=\"float: right; font-size: 100%;border-style: double;border-color: aquamarine;\"> +"+String.format("%,.0f", Patient_Differential)+"% Increase</span>";
//                }
//                }
//            }else{
//                //DECREASE
//                Patient_difference = PatientCountOverAll_LAST_MONTHLY_AVG - PatientCountOverAll_MONTHLY_AVG;
//                if(PatientCountOverAll_MONTHLY_AVG==0){
//                    Differential_badge = "<span class=\"badge badge-pill bg-danger blink_me\" style=\"float: right; font-size: 100%; border-style: double;border-color: #fa0b20;\"> -100% Decrease</span>";
//                }else{
//                    Patient_Differential = (((double)Patient_difference/(double)PatientCountOverAll_MONTHLY_AVG))*100;
//                    if(Patient_Differential==0){
//                        CDRList.append("<td  style=\"border: 1px solid black;\" > <span class=\"badge badge-pill  blink_me\" style=\"font-size: 100%;border-style: double;\">  "+String.format("%,.0f", Patient_Differential)+"%</span> </td>\n");
//                    }else{
//                        Differential_badge = "<span class=\"badge badge-pill bg-danger blink_me\" style=\"float: right; font-size: 100%; border-style: double;border-color: #fa0b20;\"> -"+String.format("%,.0f", Patient_Differential)+"% Decrease</span>";
//                    }
//                }
//            }
            Parser.SetField("Differential_badge", String.valueOf(Differential_badge));

            //TODAY
            Parser.SetField("OrdersRegistered_TODAY", String.valueOf(OrdersRegistered_TODAY));
            Parser.SetField("OrdersDispatched_TODAY", String.valueOf(OrdersDispatched_TODAY));
            Parser.SetField("OrdersReceivedAtLab_TODAY", String.valueOf(OrdersReceivedAtLab_TODAY));
            Parser.SetField("OrdersResultAnnounced_TODAY", String.valueOf(String.format("%,.0f", OrdersResultAnnounced_TODAY)));
            Parser.SetField("OrdersBacklog_TODAY", String.valueOf(String.format("%,.0f", OrdersBacklog_TODAY)));
            Parser.SetField("SuccessRatio_TODAY", String.valueOf(String.format("% ,.2f", SuccessRatio_TODAY)) + "%");
            Parser.SetField("BackLogRatio_TODAY", String.valueOf(String.format("% ,.2f", BackLogRatio_TODAY)) + "%");


            //WEEKLY
            Parser.SetField("OrdersRegistered_WEEKLY", String.valueOf(OrdersRegistered_WEEKLY));
            Parser.SetField("OrdersDispatched_WEEKLY", String.valueOf(OrdersDispatched_WEEKLY));
            Parser.SetField("OrdersReceivedAtLab_WEEKLY", String.valueOf(OrdersReceivedAtLab_WEEKLY));
            Parser.SetField("OrdersResultAnnounced_WEEKLY", String.valueOf(String.format("%,.0f", OrdersResultAnnounced_WEEKLY)));
            Parser.SetField("OrdersBacklog_WEEKLY", String.valueOf(String.format("%,.0f", OrdersBacklog_WEEKLY)));
            Parser.SetField("SuccessRatio_WEEKLY", String.valueOf(String.format("% ,.2f", SuccessRatio_WEEKLY)) + "%");
            Parser.SetField("BackLogRatio_WEEKLY", String.valueOf(String.format("% ,.2f", BackLogRatio_WEEKLY)) + "%");


            //MONTHLY
            Parser.SetField("OrdersRegistered_MONTHLY", String.valueOf(OrdersRegistered_MONTHLY));
            Parser.SetField("OrdersDispatched_MONTHLY", String.valueOf(OrdersDispatched_MONTHLY));
            Parser.SetField("OrdersReceivedAtLab_MONTHLY", String.valueOf(OrdersReceivedAtLab_MONTHLY));
            Parser.SetField("OrdersResultAnnounced_MONTHLY", String.valueOf(String.format("%,.0f", OrdersResultAnnounced_MONTHLY)));
            Parser.SetField("OrdersBacklog_MONTHLY", String.valueOf(String.format("%,.0f", OrdersBacklog_MONTHLY)));
            Parser.SetField("SuccessRatio_MONTHLY", String.valueOf(String.format("% ,.2f", SuccessRatio_MONTHLY)) + "%");
            Parser.SetField("BackLogRatio_MONTHLY", String.valueOf(String.format("% ,.2f", BackLogRatio_MONTHLY)) + "%");

            Parser.SetField("OrdersCurrentWeekDaily", String.valueOf(OrdersCurrentWeekDaily));

            Parser.SetField("OrdersCurrentMonthDaily", String.valueOf(OrdersCurrentMonthDaily));

            Parser.SetField("OrderCountMonthly", String.valueOf(OrderCountMonthly));

            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/LabDashboard.html");
        } catch (Exception var11) {
            Services.DumException("DashBoardOrange", "MainGraphicalDashboard ", request, var11);
            out.println(var11.getMessage());
            String str = "";
            for (int i = 0; i < var11.getStackTrace().length; ++i) {
                str = str + var11.getStackTrace()[i] + "<br>";
            }
            out.println(str);
            out.flush();
            out.close();
        }
    }


    void BillingDashboard(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        try {

            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            int OrdersReceivedAtLab = 0;
            int OrdersResultAnnounced = 0;
            int OrdersBilled = 0;


            Query = "Select COUNT(*) from " + Database + ".TestOrder a " +
                    "where StageIdx=2";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                OrdersReceivedAtLab = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from " + Database + ".Tests a" +
                    " INNER JOIN " + Database + ".TestOrder b ON b.Id = a.OrderId" +
                    " where  a.TestStatus is not null";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                OrdersResultAnnounced = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            Query = "Select COUNT(*) from " + Database + ".Tests a" +
                    " INNER JOIN " + Database + ".TestOrder b ON b.Id = a.OrderId" +
                    " where b.Status=10 ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                OrdersBilled = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("OrdersResultAnnounced", String.valueOf(OrdersResultAnnounced));
            Parser.SetField("OrdersReceivedAtLab", String.valueOf(OrdersReceivedAtLab));
            Parser.SetField("OrdersBilled", String.valueOf(OrdersBilled));

            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/BillingDashboard_ROVERLAB.html");
        } catch (Exception var11) {

            out.println(var11.getMessage());
            out.flush();
            out.close();
        }
    }


    void BillingDashboard_DATEWISE(final HttpServletRequest request, final PrintWriter out, final Connection conn, final ServletContext servletContext, final String UserId, final String Database, final int ClientId) {
        try {

            Statement stmt = null;
            ResultSet rset = null;
            String Query = "";
            int OrdersReceivedAtLab = 0;
            int OrdersResultAnnounced = 0;
            int OrdersBilled = 0;

            String FromDate = request.getParameter("FromDate").trim();
            String ToDate = request.getParameter("ToDate").trim();


            Query = "Select COUNT(*) from " + Database + ".TestOrder a " +
                    "where StageIdx=2 " +
                    "AND OrderDate between '" + FromDate + " 00:00:00' and '" + ToDate + " 23:59:59' ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                OrdersReceivedAtLab = rset.getInt(1);
            }
            rset.close();
            stmt.close();

            Query = "Select COUNT(*) from " + Database + ".Tests a" +
                    " INNER JOIN " + Database + ".TestOrder b ON b.Id = a.OrderId" +
                    " where  a.TestStatus is not null " +
                    "AND OrderDate between '" + FromDate + " 00:00:00' and '" + ToDate + " 23:59:59' ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                OrdersResultAnnounced = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            Query = "Select COUNT(*) from " + Database + ".Tests a" +
                    " INNER JOIN " + Database + ".TestOrder b ON b.Id = a.OrderId" +
                    " where b.Status=10 " +
                    "AND OrderDate between '" + FromDate + " 00:00:00' and '" + ToDate + " 23:59:59' ";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                OrdersBilled = rset.getInt(1);
            }
            rset.close();
            stmt.close();


            Parsehtm Parser = new Parsehtm(request);
            Parser.SetField("OrdersResultAnnounced", String.valueOf(OrdersResultAnnounced));
            Parser.SetField("OrdersReceivedAtLab", String.valueOf(OrdersReceivedAtLab));
            Parser.SetField("OrdersBilled", String.valueOf(OrdersBilled));
            Parser.SetField("FromDate", String.valueOf(FromDate));
            Parser.SetField("ToDate", String.valueOf(ToDate));
            Parser.GenerateHtml(out, Services.GetHtmlPath(servletContext) + "Forms/BillingDashboard_ROVERLAB.html");
        } catch (Exception var11) {

            out.println(var11.getMessage());
            out.flush();
            out.close();
        }
    }

}
